package dk.itu.bdm.mailreader;

import java.io.*;
import java.util.*;

import javax.mail.*;

public class App extends Thread {
	private final MailAcc mailAcc;
	private final Properties props;
	private final int loglevel;

	public App(MailAcc ma, Properties prop, int loglvl) {
		mailAcc = ma;
		loglevel = loglvl;
		props = prop;
		System.out.println("initialized thread with " + ma.getAccount());
	}

	public void run() {
		try {
			Session session = Session.getInstance(props, null);
			Store store = session.getStore();
			store.connect(mailAcc.getImapAdress(), mailAcc.getAccount(),
					mailAcc.getPass());
			Folder[] f = store.getDefaultFolder().list("*");
			collectTotals(f);

			if (loglevel == 2) {
				traverseFolders(f);
//				System.out.println(mailAcc.getAccount() + " have "
//						+ conv.size() + " converstations:");
//				conv.forEach((k, v) -> System.out.println(k + ":\t" + v.size()));
			}

			System.out.println(mailAcc);
		} catch (Exception mex) {
			mex.printStackTrace();
		}

	}

	
	private void collectTotals(Folder[] f) throws MessagingException {
		int totalCount = 0;
		for (javax.mail.Folder folder : f) {
			try {
				folder.open(Folder.READ_ONLY);
//				System.out.println(folder.getMessageCount());
				int n = folder.getMessageCount();
				if (n > 1) {
					Message message = folder.getMessage(n);
					mailAcc.setLastMail(message.getReceivedDate());
					message = folder.getMessage(1);
					mailAcc.setFirstMail(message.getReceivedDate());

				}
				if ((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {
					totalCount += folder.getMessageCount();
					// Account for the mails sent
					if (folder.getFullName().matches(
							".*[sS]ent.*|.*[sS]end.*|.*[Uu]dbakke.*")) {
						mailAcc.setMailsSent(folder.getMessageCount());
						mailAcc.setSendMailFolderName(folder.getFullName());
					 System.out.println("found the sent folder: "+folder.getFullName()+folder.getMessageCount());
					}
				}
			} catch (MessagingException e) {
//				e.printStackTrace();
			}
			if (folder.isOpen()) {
				folder.close(false);
			}
		}

		mailAcc.setMailsTotal(totalCount);
	}

	public static void main(String[] args) throws FileNotFoundException {
		if (args.length == 0) {
			System.out
					.println("you need to point to your mailcredential file as the argument");
			System.out
					.println("the file should be in the order: \"imapadress account password\"");
			System.exit(3);
		}
		int loglvl;

		loglvl = args.length == 2 ? Integer.parseInt(args[1]) : 1;
		Properties props = new Properties();
		props.setProperty("mail.store.protocol", "imaps");
		Scanner sc = new Scanner(new File(args[0]));
		ArrayList<MailAcc> accounts = new ArrayList<MailAcc>();
		while (sc.hasNext()) {
			accounts.add(new MailAcc(sc.next(), sc.next(), sc.next()));
		}
		System.out
				.println("Searching through " + accounts.size() + " accounts");
		for (MailAcc mailAcc : accounts) {
			new Thread(new App(mailAcc, props, loglvl)).start();
		}
		sc.close();
	}
private void traverseFolders(Folder[] f) throws MessagingException {
	//then add all the rest
		for (javax.mail.Folder folder : f) {
			if(!folder.isOpen()){
			try {
				folder.open(Folder.READ_ONLY);
			} catch (Exception e) {
				e.printStackTrace();
			}}
			Message[] msgs = folder.getMessages();
			System.out
					.println("starting collecting statistics in folder "+folder.getName()+" This may take a while");
			for (Message msg : msgs) {
					try {
						mailAcc.putEmail(msg, folder.getFullName().equalsIgnoreCase(mailAcc.getSentFolderName()));
					} catch (NullPointerException | IndexOutOfBoundsException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
				
				
			}
			folder.close(false);
		}
		System.out.println(mailAcc.cleanConversation());
		mailAcc.printStatistics();
	}

}