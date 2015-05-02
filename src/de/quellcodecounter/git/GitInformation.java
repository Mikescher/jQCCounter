package de.quellcodecounter.git;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

public class GitInformation {
	public class GitInformationCommit {
		public String Checksum;
		public String Author;
		public String Mail;
		public Date Date;
		public String Message;
		
		@Override
		public String toString() {
			return Checksum + "(" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Date) + ")\r\n" + Author + " <" + Mail + ">\r\n" + Message;
		}
	}
	
	public class GitInformationRemote {
		public String branch = null;
		public String uri = null;
		public String link = null;
		public String name = null;
		
		public int ahead = 0;
		public int behind = 0;
	}
	
	public boolean isRepository = false;
	
	public String branchFull = null;
	public String branchShort = null;
	public String state = null;
	public GitInformationRemote remote;
	public List<GitInformationCommit> commits = new ArrayList<>();
	
	public GitInformation() {
		
	}
	
	public boolean load(File repoFile) {
		Repository repository;
		Git git = null;
		
		try {
			repository = new FileRepositoryBuilder().setGitDir(repoFile)
					.setMustExist(true)
					.readEnvironment()
					.build();
		    git = new Git(repository);

		    branchFull = repository.getFullBranch();
		    branchShort = repository.getBranch();
		    state = repository.getRepositoryState().getDescription();

			for (Ref branch : git.branchList().call()) {
				if (branch.getName().equals(branchFull)) {
					BranchTrackingStatus bts = BranchTrackingStatus.of(repository, branchFull); 
					
					if (bts != null) {
						RemoteConfig rc = new RemoteConfig(repository.getConfig(), repository.getRemoteName(bts.getRemoteTrackingBranch()));
						
						for (URIish uri : rc.getURIs()) {
							if (uri.isRemote() && uri.getHost().toLowerCase().equals("github.com")) {
								remote = new GitInformationRemote();
								
								remote.branch = bts.getRemoteTrackingBranch();
								remote.uri = uri.toString();
								remote.link = "http://" + uri.getHost() + uri.getPath().replace(".git", "");
								remote.name = uri.getHumanishName();
								
								remote.ahead = bts.getAheadCount();
								remote.behind = bts.getBehindCount();
							}
						}
					}

					commits = loadCommits(repository, git, branch);
				}
			}
		    
		    git.close();
		} catch (IOException | GitAPIException | URISyntaxException e) {
			e.printStackTrace();
			
			isRepository = false;
			
			if (git != null) git.close();
			return false;
		}

		isRepository = true;
		return true;
	}

	protected List<GitInformationCommit> loadCommits(Repository repository, Git git, Ref branch) throws GitAPIException, NoHeadException, IOException, MissingObjectException, IncorrectObjectTypeException, AmbiguousObjectException {
		RevWalk revwalk = new RevWalk(repository);
		
		List<GitInformationCommit> result = new ArrayList<>();
		
		for (Ref rbranch : git.branchList().call()) {
			if (rbranch.getName().equals(branch.getName())) {
		        Iterable<RevCommit> commits = git.log().all().call();

		        for (RevCommit commit : commits) {
		            boolean foundInThisBranch = false;

		            RevCommit targetCommit = revwalk.parseCommit(repository.resolve(commit.getName()));
		            for (Map.Entry<String, Ref> e : repository.getAllRefs().entrySet()) {
		                if (e.getKey().startsWith(Constants.R_HEADS)) {
		                    if (revwalk.isMergedInto(targetCommit, revwalk.parseCommit(e.getValue().getObjectId()))) {
		                        String foundInBranch = e.getValue().getName();
		                        if (branch.getName().equals(foundInBranch)) {
		                            foundInThisBranch = true;
		                            break;
		                        }
		                    }
		                }
		            }

		            if (foundInThisBranch) {
		            	GitInformationCommit cmt = new GitInformationCommit();
		            	
		            	cmt.Checksum = commit.getName();
		            	cmt.Author = commit.getAuthorIdent().getName();
		            	cmt.Mail = commit.getAuthorIdent().getEmailAddress();
		            	cmt.Date = new Date(commit.getCommitTime());
		            	cmt.Message = commit.getFullMessage();
		            	
		            	result.add(cmt);
		            }
		        }
			}
		}
		
		return result;
	}

}
