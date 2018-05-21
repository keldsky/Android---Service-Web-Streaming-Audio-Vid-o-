package musique;
import Ice.Current;
import MusiqueStream.*;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventAdapter;
import uk.co.caprica.vlcj.player.list.MediaListPlayerMode;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

public class Serveur extends _GestionMusiqueStreamDisp  {

	 MediaPlayerFactory factory;

	    MediaListPlayer mediaListPlayer;
	      
	    private JFrame frame;

	    MediaList playList;
	    
	    String lienstream;
	    
	    int stream = 0;
	    
	    public  Serveur() {
	    
	        boolean found = new NativeDiscovery().discover();
	        System.out.println(LibVlc.INSTANCE.libvlc_get_version());
	        factory = new MediaPlayerFactory();
	        mediaListPlayer = factory.newMediaListPlayer();
	        mediaListPlayer.addMediaListPlayerEventListener(new MediaListPlayerEventAdapter() {
	            @Override
	            public  void nextItem(MediaListPlayer mediaListPlayer, libvlc_media_t item, String itemMrl) {
	                System.out.println("Playing next item: " + itemMrl + " (" + item + ")");
	            }
	        });
	        
	    
	       
	        
	        playList = factory.newMediaList();
	    }
	    
	    /**
	     * Return url that streams music the user want 
	     */
	@Override
	public String Stream(String command, String musique, Current __current) {

		 String dir = null;
		 String address = null;
		 int  port ;
		 //broadcast address
		 address="192.168.42.255";
         port=5555;
   
         
  if(command.equals("play")==true)
  {
	      //location of music on the disk
	      dir="D:\\Zik\\"+musique;
	  
	 
		            try {
		            	
		            	//Start streaming of the music
						start(dir, address, port ,musique);
						
				        //adress of streaming that will be return
						address="rtp://192.168.42.255:5555";
						
				       	
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
  }
 
    
       return address;
		       
		 
		
	}
	/**
	 * Delete music from the playlist
	 */
	@Override
	public void Delete(String musique, Current __current) {
		
		    String directoryPath = "D:\\Zik\\"+musique;
			File file = new File(directoryPath);
			
			boolean exists = file.exists();
			
			if(exists==true)
			{

			try {
				//Deleting the directory recursively.
				folderdelete(file);
				System.out.println("Sucesss !");
			} catch (IOException e) {
				System.out.println("Failed  : " + directoryPath);
				e.printStackTrace();
			}
			
			}
		
	}
	/**
	 * Delete folder where music is located
	 * 
	 * @param file
	 * @throws IOException
	 */
	private static void folderdelete(File file) throws IOException {
		 
		for (File childFile : file.listFiles()) {
 
			if (childFile.isDirectory()) {
				folderdelete(childFile);
			} else {
				if (!childFile.delete()) {
					throw new IOException();
				}
			}
		}
 
		if (!file.delete()) {
			throw new IOException();
		}
	}
	

    
/**
 * Start streaming of music by adding music to the playlist
 * 
 * @param dir
 * @param address
 * @param port
 * @param musique
 * @throws Exception
 */
    private  void start(String dir, String address, int port,String musique) throws Exception {
    	
    	//new NativeDiscovery().discover();
    	if(mediaListPlayer.isPlaying()){
    		mediaListPlayer.stop();
    		mediaListPlayer.release();
    	}
    	//new NativeDiscovery().discover();
    	mediaListPlayer = factory.newMediaListPlayer();
        System.out.println("Scanning for audio files...");
        
        // Scan for media files
        List<File> files = scanForMedia(dir);
        
        // Randomise the order
        Collections.shuffle(files);
        
        // Prepare the media options for streaming
        String mediaOptions = formatRtpStream(address, port);
        
        // Add each media file to the play-list...
        playList.release();
        playList = factory.newMediaList();
        for(File file : files) {
            // You could instead set standard options on the media list player rather
            // than setting options each time you add media
            playList.addMedia(file.getAbsolutePath(), mediaOptions);
        }
        
        // Attach the play-list to the media list player
        mediaListPlayer.setMediaList(playList);
        
        // Finally, start the media player
        mediaListPlayer.play();
        System.out.println("Streaming started at rtp://" + address + ":" + port);
	    
    }

    /**
     * Search a directory, recursively, for mp3 files.
     *
     * @param root root directory
     * @return collection of mp3 files
     */
    private  List<File> scanForMedia(String root) {
        List<File> result = new ArrayList<File>(400);
        scanForMedia(new File(root), result);
        return result;
    }

    private  void scanForMedia(File root, List<File> result) {
        if(root.exists() && root.isDirectory()) {
        	
            // List all matching mp3 files...
            File[] files = root.listFiles(new FileFilter() {
                @Override
                public   boolean accept(File pathname) {
                    return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".mp3");
                }
            });
            
            // Add them to the collection
            result.addAll(Arrays.asList(files));
            
            // List all nested directories...
            File[] dirs = root.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            
            // Recursively scan each nested directory...
            for(File dir : dirs) {
                scanForMedia(dir, result);
            }
        }
    }
    
/**
 * 
 * @param serverAddress
 * @param serverPort
 * @return  mediaOptions for rtp stream wih vlc
 */
    private  String formatRtpStream(String serverAddress, int serverPort) {
        StringBuilder sb = new StringBuilder(60);
        sb.append(":sout=#rtp{dst=");
        sb.append(serverAddress);
        sb.append(",port=");
        sb.append(serverPort);
        sb.append(",mux=ts}");
        return sb.toString();
    }
    



}
