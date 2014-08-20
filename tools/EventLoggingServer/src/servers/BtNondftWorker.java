/**
 * @author Junxian Huang
 * @date Aug 31, 2009
 * @time 5:39:21 PM
 * @organization University of Michigan, Ann Arbor
 */
package servers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import common.BaseWorker;
import common.Definition;
import common.PrefixParser;

/**
 * @author Junxian Huang
 *
 */
public class BtNondftWorker extends BaseWorker {
	
	public void run() {

		try {
			
			client.setSoTimeout(Definition.RECV_TIMEOUT);

			// String line = "";
			BufferedReader in = null;
			PrintWriter out = null;
			char buffer[] = new char[20480];

			// //////////////////////////////////////////
			// Common init

			this.id = this.getId();

			System.out.println("<" + id + "> BtNondft Thread starts");

			in = new BufferedReader(new InputStreamReader(client
					.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);

			// //////////////////////////////////////////
			// Get prefix and send prefix ok

			
			StringBuilder prefix_sb = new StringBuilder("");
			int bytes_read = in.read(buffer);
			prefix_sb.append(buffer, 0, bytes_read);
			String prefix = prefix_sb.toString();
			System.out.println("prefix:" + prefix_sb.toString());

			// String prefix = "<iPhone><device_id><run_id>";

			PrefixParser parser = new PrefixParser();
			String prefix_array_string = parser.parsePrefix(prefix);
			if (prefix_array_string == null) {
				System.out.println("Thread <" + id + ">: Prefix error "
						+ prefix);
				return;
			}
			String[] prefix_array = prefix_array_string.split("@@@HJX@@@");
			type_string = prefix_array[0];
			id_string = prefix_array[1];
			rid_string = prefix_array[2];
			
			out.print("PrefixOK");
			out.flush();
			
			
			// //////////////////////////////////////////
			// Branching
			
			prefix_sb = new StringBuilder("");
			bytes_read = in.read(buffer);
			prefix_sb.append(buffer, 0, bytes_read);
			String exp_string = prefix_sb.toString();
			
			
			
			int offset = 0;
		    int numRead = 0;
		    byte[] server_binbytes = null;
		    byte[] recv_byte_buffer = new byte[20480];
		    
			
			
			
			if(exp_string.equals("Downlink")){
				//Downlink experiment
				
				//Bash tcpdump
				String s = null;
				Process p = Runtime.getRuntime().exec(
						"bash btinit.sh " + type_string + " " + id_string + " "
								+ rid_string + "downlink_random");
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
					if (s.startsWith("btinit ok")) {
						break;
					}
				}
				
				
				//read in server bin
				//Read in server bin file
				File server_bin = new File("btsvr.bin");
				//File server_bin = new File("/home/3gtest/3G_servers_2.0/server/Bt/btsvr.bin");
				InputStream server_bin_is = new FileInputStream(server_bin);
				
				
		        // Get the size of the file
		        long server_bin_length = server_bin.length();
		    
		        // Create the byte array to hold the data
		        server_binbytes = new byte[(int)server_bin_length + 10];
		        System.out.println("BT server size " + server_bin_length);
		    
		        // Read in the bytes
		       
		        while (offset < server_binbytes.length
		               && (numRead = server_bin_is.read(server_binbytes, offset, server_binbytes.length - offset)) > 0) {
		            offset += numRead;
		            System.out.println("offset read " + numRead);
		        }
		        //if (offset < server_binbytes.length) {
		        //    throw new IOException("Could not completely read file " + server_bin.getName());
		        //}
		        server_bin_is.close();
				
				
				
				out.print("DownlinkOK");
				out.flush();
				
				
				//Start main
				
		        
		        offset = 0;
		        FileReader sz_fr = new FileReader("bt.sz");
		        //FileReader sz_fr = new FileReader("/home/3gtest/3G_servers_2.0/server/Bt/bt.sz");
		        BufferedReader sz_br = new BufferedReader(sz_fr);
		        String sz_line = "";
		        while(sz_line != null){
		        	if(sz_line.startsWith("c")){

		        		String[] parts = sz_line.split(" ");
		        		int recv_size = Integer.parseInt(parts[1]);
		        		bytes_read = 0;
		        		
		        		int res = 0;
		        		while(bytes_read < recv_size){
		        			res = client.getInputStream().read(recv_byte_buffer);
		        			if(res < 0){
		        				break;
		        			}
		        			bytes_read += res; 
		        			//System.out.println("nondft bt recving");
		        		}
		        		//System.out.println("reach here?");
		        		if(!sz_line.endsWith("" + bytes_read)){
		        			System.out.println("Error BT downlink : " + sz_line + " . " + bytes_read);
		        		}
		        		
		        	}else if(sz_line.startsWith("s")){
		        		String[] parts = sz_line.split(" ");
		        		int send_size = Integer.parseInt(parts[1]);
		        		client.getOutputStream().write(server_binbytes, offset, send_size);
		        		client.getOutputStream().flush();
		        		offset += send_size;
		        	}
		        	
		        	sz_line = sz_br.readLine();
		        }
		        
		        
		        sz_br.close();
		        sz_fr.close();


				
				
				
				
			}else if(exp_string.equals("UplinkStart")){
				//Uplink experiment
				//Bash tcpdump
				String s = null;
				Process p = Runtime.getRuntime().exec(
						"bash btinit.sh " + type_string + " " + id_string + " "
								+ rid_string + "uplink_random");
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
					if (s.startsWith("btinit ok")) {
						break;
					}
				}
				
				//read in server bin
				//Read in server bin file
				File server_bin = new File("bt.bin");
				//File server_bin = new File("/home/3gtest/3G_servers_2.0/server/Bt/bt.bin");
				InputStream server_bin_is = new FileInputStream(server_bin);
				
				
		        // Get the size of the file
		        long server_bin_length = server_bin.length();
		    
		        // Create the byte array to hold the data
		        server_binbytes = new byte[(int)server_bin_length + 10];
		        System.out.println("BT server size " + server_bin_length);
		    
		        // Read in the bytes
		       
		        while (offset < server_binbytes.length
		               && (numRead = server_bin_is.read(server_binbytes, offset, server_binbytes.length - offset)) > 0) {
		            offset += numRead;
		            System.out.println("offset read " + numRead);
		        }
		        //if (offset < server_binbytes.length) {
		        //    throw new IOException("Could not completely read file " + server_bin.getName());
		        //}
		        server_bin_is.close();
				
			
				//Start main
				
		        
		        offset = 0;
		        FileReader sz_fr = new FileReader("bt.sz");
		        //FileReader sz_fr = new FileReader("/home/3gtest/3G_servers_2.0/server/Bt/bt.sz");
		        BufferedReader sz_br = new BufferedReader(sz_fr);
		        String sz_line = "";
		        while(sz_line != null){
		        	if(sz_line.startsWith("s")){

		        		String[] parts = sz_line.split(" ");
		        		int recv_size = Integer.parseInt(parts[1]);
		        		bytes_read = 0;
		        		int res = 0;
		        		while(bytes_read < recv_size){
		        			res = client.getInputStream().read(recv_byte_buffer);
		        			if(res < 0){
		        				break;
		        			}
		        			bytes_read += res; 
		        			//System.out.println("nondft bt recving");
		        		}
		        		
		        		//System.out.println("reach here?");
		        		if(!sz_line.endsWith("" + bytes_read)){
		        			System.out.println("BT uplink : " + sz_line + " . actual " + bytes_read);
		        		}
		        		
		        	}else if(sz_line.startsWith("c")){
		        		String[] parts = sz_line.split(" ");
		        		int send_size = Integer.parseInt(parts[1]);
		        		client.getOutputStream().write(server_binbytes, offset, send_size);
		        		client.getOutputStream().flush();
		        		offset += send_size;
		        	}
		        	
		        	sz_line = sz_br.readLine();
		        }
		        
		        
		        sz_br.close();
		        sz_fr.close();
				
				
				
				
			}
			
			
			in.close();
			out.close();
			client.close();

			// //////////////////////////////////////////
			// Terminate tcpdump

			String s2 = null;
			//System.out.println("<Thread " + id + "> bash uprep.sh "
			//		+ type_string + " " + id_string + " " + rid_string);
			Process p2 = Runtime.getRuntime().exec(
					"bash btrep.sh " + type_string + " " + id_string + " "
							+ rid_string);
			BufferedReader stdInput2 = new BufferedReader(
					new InputStreamReader(p2.getInputStream()));
			while ((s2 = stdInput2.readLine()) != null) {
				System.out.println(s2);
				if (s2.startsWith("btrep ok")) {
					break;
				}
			}

			System.out.println("<" + id + "> Thread ends");

		} catch (IOException e) {
			e.printStackTrace();
			String s2 = null;
			//System.out.println("<Thread " + id + "> bash uprep.sh "
			//		+ type_string + " " + id_string + " " + rid_string);
			try {
				Process p2 = Runtime.getRuntime().exec(
						"bash btrep.sh " + type_string + " " + id_string + " "
								+ rid_string);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}