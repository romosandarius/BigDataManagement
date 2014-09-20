package dk.itu.bdm.mailreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.mail.*;

public class App extends Thread {
	private final MailAcc mailAcc;
	private final Properties props;
	private ConcurrentHashMap<String, Integer> emailPerAdress;
	private ConcurrentHashMap<String, ArrayList<Message>> conversations;
	private final int loglevel;

	public App(MailAcc ma, Properties prop, int loglvl) {
		mailAcc = ma;
		loglevel = loglvl;
		props = prop;
		emailPerAdress = new ConcurrentHashMap<String, Integer>();
		System.out.println("initialized thread with " + ma.getAccount());
	}

	public void run() {
		try {
			Session session = Session.getInstance(props, null);
			Store store = session.getStore();
			store.connect(mailAcc.getImapAdress(), mailAcc.getAccount(),
					mailAcc.getPass());
			int totalCount = 0;
			Folder[] f = store.getDefaultFolder().list("*");
			for (javax.mail.Folder folder : f) {
				try {
					folder.open(Folder.READ_ONLY);
					System.out.println(folder.getMessageCount());
					int n = folder.getMessageCount();
					if (n > 1) {
						Message message = folder.getMessage(n);
						// System.out.println(message.getSubject()+message.getReceivedDate().toString());
						mailAcc.setLastMail(message.getReceivedDate());
						message = folder.getMessage(1);
						// System.out.println(message.getSubject()+message.getReceivedDate().toString());
						mailAcc.setFirstMail(message.getReceivedDate());
						Message[] msgs = folder.getMessages();
						if (loglevel == 2)
							traverseMessages(msgs);

					}
					if ((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {
						totalCount += folder.getMessageCount();
						// Account for the mails sent
						if (folder.getFullName().matches(
								".*[sS]ent.*|.*[sS]end.*|.*[Uu]dbakke.*")) {
							mailAcc.setMailsSent(folder.getMessageCount());
							 System.out.println("found the sent folder: "+folder.getFullName()+folder.getMessageCount());
						}
					}
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
			if (loglevel == 2) {
				ConcurrentHashMap<String, ArrayList<Message>> conv = mailAcc
						.cleanConversations();
				System.out.println(mailAcc.getAccount() + " have "
						+ conv.size() + " converstations:");
				conv.forEach((k, v) -> System.out.println(k + ":\t" + v.size()));
			}
			mailAcc.setMailsTotal(totalCount);
			// System.out.println("total count= " + totalCount);
			System.out.println(mailAcc);
		} catch (Exception mex) {
			mex.printStackTrace();
		}

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

		loglvl = args.length == 4 ? Integer.parseInt(args[3]) : 1;
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

	public void traverseMessages(Message[] folder) {
		String from;
		int msgCount;
		System.out
				.println("starting collecting statistics about ppl. This may take a while");
		int no = 0;
		for (Message msg : folder) {
			try {
				mailAcc.addEmailToConversation(msg);
				from = msg.getFrom()[0].toString();
				msgCount = (emailPerAdress.get(from) != null) ? emailPerAdress
						.get(from) + 1 : 1;
				emailPerAdress.put(from, msgCount);
			} catch (ArrayIndexOutOfBoundsException e) {
			} catch (NullPointerException e) {
			} catch (MessagingException e) {
			}
		}

	}
}