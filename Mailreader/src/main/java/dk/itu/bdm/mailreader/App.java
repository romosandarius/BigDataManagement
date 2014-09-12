package dk.itu.bdm.mailreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import javax.mail.*;

public class App {
	public static void main(String[] args) throws FileNotFoundException {
		Properties props = new Properties();
		props.setProperty("mail.store.protocol", "imaps");
		Scanner sc = new Scanner(new File("mails.acc"));		
		ArrayList<MailAcc> accounts= new ArrayList<MailAcc>();
		while (sc.hasNext()) {
			accounts.add(new MailAcc(sc.next(), sc.next(), sc.next()));
		}
		System.out.println("Searching through "+accounts.size()+" accounts");
		for (MailAcc mailAcc : accounts) {
			try {
				Session session = Session.getInstance(props, null);
				Store store = session.getStore();
				store.connect(mailAcc.getImapAdress(),mailAcc.getAccount(),mailAcc.getPass());
				int totalCount = 0;
				Folder[] f = store.getDefaultFolder().list("*");
				for (javax.mail.Folder folder : f) {
					try {
						folder.open(Folder.READ_ONLY);
						int n=folder.getMessageCount();
						if (n>1){
						Message message= folder.getMessage(n);
//						System.out.println(message.getSubject()+message.getReceivedDate().toString());
						mailAcc.setLastMail(message.getReceivedDate());
						message=folder.getMessage(1);
//						System.out.println(message.getSubject()+message.getReceivedDate().toString());
						mailAcc.setFirstMail(message.getReceivedDate());
						}
						if ((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {
							totalCount += folder.getMessageCount();
							//Account for the mails sent
							if(folder.getFullName().matches(".*[sS]ent.*|.*[sS]end.*|.*[Uu]dbakke.*")){
								mailAcc.setMailsSent(folder.getMessageCount());
								System.out.println("found the sent folder: "+folder.getFullName());
							}
						}
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
				}
				mailAcc.setMailsTotal(totalCount);
//				System.out.println("total count= " + totalCount);
				System.out.println(mailAcc);
				sc.close();
			} catch (Exception mex) {
				mex.printStackTrace();
			}
		}
	}
}