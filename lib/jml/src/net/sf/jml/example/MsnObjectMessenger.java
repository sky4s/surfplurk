package net.sf.jml.example;

import net.sf.jml.*;
import net.sf.jml.event.MsnAdapter;
import net.sf.jml.message.p2p.DisplayPictureRetrieveWorker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/**
 * This class downloads all active contacts avatars.
 * 
 * @author Angel Barragán Chacón
 */
public class MsnObjectMessenger extends BasicMessenger {

	/**
	 * Instance of the log to be used.
	 */
	private static final Log log = LogFactory.getLog(MsnObjectMessenger.class);

	////////////////////////////////////////////////////////////////////////////
	
	@Override
	protected void initMessenger(MsnMessenger messenger) {
		messenger.getOwner().setInitStatus(MsnUserStatus.ONLINE);
		try {
			MsnObject displayPicture = MsnObject.getInstance(
					getEmail(),
					"./resource/UserTile/headset.png");
			messenger.getOwner().setInitDisplayPicture(displayPicture);
		} catch (Exception ex) {
			log.warn("can't load user tile.",ex);
		}
		messenger.addListener(new MsnAdapter() {

	        public void contactListInitCompleted(MsnMessenger messenger) {
	            log.info(messenger + " contact list init completeted");
	            retrieveContactsAvatars(messenger);
	        }

			@Override
			public void exceptionCaught(MsnMessenger messenger, 
					                    Throwable throwable) {
	            log.error(messenger + throwable.toString(), throwable);
			}

			@Override
			public void logout(MsnMessenger messenger) {
	            log.info(messenger + " logout");
			}
	        
		});
	}
	
	////////////////////////////////////////////////////////////////////////////

	/**
	 * List of contacts.
	 */
	private MsnContact[] contacts;
	
	/**
	 * Index of the processend contact.
	 */
	private int index = 0;
	
	/**
	 * Number of retrieved avatars.
	 */
	private int processed = 0;

	private int MAX_AVATAR_RETRIEVED = 500;
	
	/**
	 * MsnContacts with null avatars.
	 */
	private Set<String> nullAvatars = new HashSet<String>();

	/**
	 * MsnContacts with unavailable avatars.
	 */
	private Set<String> unavailableAvatars = new HashSet<String>();

	/**
	 * MsnContacts whose avataras has been retrieved.
	 */
	private Set<String> goodAvatars = new HashSet<String>();
	
	/**
	 * Process all contacts and retrieve their avatar.
	 * 
	 * @param messenger Instance of the messenger we are working on. 
	 */
	private void retrieveContactsAvatars(MsnMessenger messenger) {

		contacts = messenger.getContactList().getContacts();
		index = 0;
        // look for someone
        //  for (int i = 0 ; i < contacts.length ; i++) {
        //      if (contacts[i].getEmail().equals(
        //          Email.parseStr("XXX@hotmail.com"))) {
        //          index = i;
        //          break;
        //      }
        //  }
        retrieveNextContactAvatar(messenger);
	}

	/**
	 * Retrieve the avatar of the next contact of the list.
	 * 
	 * @param messenger Instance of the messenger we are working on. 
	 */
	private void retrieveNextContactAvatar(MsnMessenger messenger) {
	
		while (index < contacts.length && processed < MAX_AVATAR_RETRIEVED) {
		
			// Get the contact
			MsnContact contact = contacts[index++];
			
			// Get the MSnObject
			MsnObject avatar = contact.getAvatar(); 

			// Check if it exists
			if (avatar != null) {
			
				// Retrieve the avatar
				messenger.retrieveDisplayPicture(
						avatar,
						new DisplayPictureListener() {

							public void notifyMsnObjectRetrieval(
									MsnMessenger messenger,
									DisplayPictureRetrieveWorker worker,
									MsnObject msnObject,
									ResultStatus result,
                                    byte[] resultBytes,
                                    Object context) {
								
								// Log the result
								log.info("Finished Avatar retrieval " + result);

								// Check for the value
								if (result == ResultStatus.GOOD) {
                                    // Create a new file to store the avatar
                                    File storeFile = new File (
                                            "avatar" + System.currentTimeMillis() + ".png");
                                    try {
                                        FileOutputStream storeStream = new FileOutputStream(storeFile);
                                        storeStream.write(resultBytes);
                                        goodAvatars.add(msnObject.getCreator());
                                        storeStream.close();
                                    }
                                    catch (FileNotFoundException e) {
                                        System.err.println("Critical error: Unable to find file we just created.");
                                    }
                                    catch (IOException e) {
                                        System.err.println("Critical error: Unable to write data to file system.");
                                    }
								}
								else {
									unavailableAvatars.add(
											msnObject.getCreator());
								}
								
								// Process next contact
								retrieveNextContactAvatar(messenger);
							}
						});
				
				// Stop launching workers
				processed++;
				break;
			}
			else {
				nullAvatars.add(contact.getEmail().getEmailAddress());
			}
		}
		
		// Check for finalization
		if (index >= contacts.length || processed >= MAX_AVATAR_RETRIEVED) {
			System.out.println("********* FINISHED CONTACTS AVATAR RETRIEVAL *********");
			
			// Set the list of all contacts with null avatars
			System.out.println("**** NULL Avatars ****");
			for (String contact: nullAvatars) {
				String status = messenger.getContactList().getContactByEmail(
						Email.parseStr(contact)).getStatus().toString();
				System.out.println(contact + " " + status);
			}
			
			// Set the list of contacts that couldn't retrieve the avatar
			System.out.println("**** Unavailable Avatars ****");
			for (String contact: unavailableAvatars) {
				String status = messenger.getContactList().getContactByEmail(
						Email.parseStr(contact)).getStatus().toString();
				System.out.println(contact + " " + status);
			}
			
			// Set the list of contact that we have retrieved the avatar
			System.out.println("**** Retrieved Avatars ****");
			for (String contact: goodAvatars) {
				String status = messenger.getContactList().getContactByEmail(
						Email.parseStr(contact)).getStatus().toString();
				System.out.println(contact + " " + status);
			}
			
			// Set the total of contacts and the total of processed ones
			System.out.println("Total contacts: " + 
					 messenger.getContactList().getContacts().length);
			System.out.println("Processed contacts: " + 
					           (nullAvatars.size() + 
					            unavailableAvatars.size() + 
					            goodAvatars.size() ));
			
			// Close the messenger
			messenger.logout();
		}
		
	}
	
}
