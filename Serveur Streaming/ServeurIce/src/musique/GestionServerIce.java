package musique;

public class GestionServerIce  {
	 public static void main(String[] args)
	    {
		 
		
	        int status = 0;
	        Ice.Communicator ic = null;
	        try {
	            ic = Ice.Util.initialize(args);
	            Ice.ObjectAdapter adapter =
	                ic.createObjectAdapterWithEndpoints("SimplePrinterAdapter", "tcp -h 192.168.42.239 -p 10001");
	            //Get the object Serveur
	            Ice.Object object = new Serveur();
	            adapter.add(object, ic.stringToIdentity("SimplePrinter"));
	            adapter.activate();
	            ic.waitForShutdown();
	        } catch (Ice.LocalException e) {
	            e.printStackTrace();
	            status = 1;
	        } catch (Exception e) {
	            System.err.println(e.getMessage());
	            status = 1;
	        }
	        if (ic != null) {
	            // Clean up
	            //
	            try {
	                ic.destroy();
	            } catch (Exception e) {
	                System.err.println(e.getMessage());
	                status = 1;
	            }
	        }
	        System.exit(status);
	    }
}
