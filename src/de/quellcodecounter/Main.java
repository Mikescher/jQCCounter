package de.quellcodecounter;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;

import de.quellcodecounter.gui.MainFrame;

public class Main {
	public static void main(String[] args) throws IOException, URISyntaxException, NoHeadException, GitAPIException {
		(new MainFrame()).setVisible(true);
		
//		FileRepositoryBuilder builder = new FileRepositoryBuilder();
//		Repository repository = builder.setGitDir(new File("E:\\Eigene Dateien\\Dropbox\\Programming\\Java\\workspace\\jQCCounter\\.git"))
//				.setMustExist(true)
//				.readEnvironment() // scan environment GIT_* variables
//				.build();
//	    Git git = new Git(repository);
//	    RevWalk walk = new RevWalk(repository);
//
//		System.out.println(repository.getBranch());
//		System.out.println(repository.getFullBranch());
//		System.out.println(repository.getRepositoryState().getDescription());
//		
//		System.out.println("===");
//		
//		for (Ref branch : git.branchList().call()) {
//			BranchTrackingStatus bts = BranchTrackingStatus.of(repository, branch.getName()); 
//			if (bts != null)
//				System.out.println(branch.getName() + "(" + bts.getAheadCount() + "|" + bts.getBehindCount() + ") => " + bts.getRemoteTrackingBranch());
//			else
//				System.out.println(branch.getName());
//					
//	        Iterable<RevCommit> commits = git.log().all().call();
//
//	        for (RevCommit commit : commits) {
//	            boolean foundInThisBranch = false;
//
//	            RevCommit targetCommit = walk.parseCommit(repository.resolve(commit.getName()));
//	            for (Map.Entry<String, Ref> e : repository.getAllRefs().entrySet()) {
//	                if (e.getKey().startsWith(Constants.R_HEADS)) {
//	                    if (walk.isMergedInto(targetCommit, walk.parseCommit(e.getValue().getObjectId()))) {
//	                        String foundInBranch = e.getValue().getName();
//	                        if (branch.getName().equals(foundInBranch)) {
//	                            foundInThisBranch = true;
//	                            break;
//	                        }
//	                    }
//	                }
//	            }
//
//	            if (foundInThisBranch) {
//	                System.out.println(commit.getName());
//	                System.out.println(commit.getAuthorIdent().getName());
//	                System.out.println(new Date(commit.getCommitTime()));
//	                System.out.println(commit.getFullMessage());
//	            }
//	        }
//		}
//		
//		System.out.println("===");
//
//		for (String remote : repository.getRemoteNames()) {
//			RemoteConfig rc = new RemoteConfig(repository.getConfig(), remote);
//			
//			System.out.println(remote);
//			for (URIish uri : rc.getURIs()) {
//				System.out.println("    " + uri.toString() + " ; " + uri.getHost() + " ; " + uri.getUser() + " ; " + uri.getPass() + " ; " + uri.getPath() + " ; " + uri.getHumanishName());
//			}
//		}
//
//		System.out.println("===");
//
//		
//		
//		System.exit(0);
	}
}
