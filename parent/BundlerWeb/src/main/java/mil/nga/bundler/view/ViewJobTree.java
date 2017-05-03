package mil.nga.bundler.view;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mil.nga.bundler.model.Archive;
import mil.nga.bundler.model.Job;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name="viewJobTree")
@ViewScoped
public class ViewJobTree extends BundlerEJBClient implements Serializable {

    
    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 60291018740225102L;

    
    private TreeNode root;
    
    public TreeNode getRoot(Job job) {
        root = new DefaultTreeNode("root", null);
        
        TreeNode jobId = new DefaultTreeNode(
                "Job ID: " + job.getJobID(), root);
        TreeNode archiveType = new DefaultTreeNode(
                "Type :" + job.getArchiveType(), root);
        TreeNode maxSize = new DefaultTreeNode(
                "Max Size :" + job.getArchiveSize(), root);
        TreeNode numFiles = new DefaultTreeNode(
                "Num Files :" + job.getNumFiles(), root);
        TreeNode jobState = new DefaultTreeNode(
                "Job State : " + job.getState(), root);
        
        // See if we have the fully loaded view of the Job.
        if ((job.getArchives() == null) || (job.getArchives().size() == 0)) {
            job = super.getMaterializedJob(job.getJobID());
        }
        
        if ((job.getArchives() != null) && (job.getArchives().size() > 0)) {
            TreeNode archives = new DefaultTreeNode("Archives", root);
            for (Archive current : job.getArchives()) {
                TreeNode archive = new DefaultTreeNode("Archive ID : " + current.getArchiveID(), archives);
                archive.getChildren().add(new DefaultTreeNode("URL : " + current.getArchiveURL()));
                archive.getChildren().add(new DefaultTreeNode("Hash URL : " + current.getHashURL()));
                archive.getChildren().add(new DefaultTreeNode("Num Files : " + current.getNumFiles()));
                archive.getChildren().add(new DefaultTreeNode("Size : " + current.getSizeHR()));
                archive.getChildren().add(new DefaultTreeNode("State : " + current.getArchiveState()));
            }
        }
        
        return root;
    }
}
