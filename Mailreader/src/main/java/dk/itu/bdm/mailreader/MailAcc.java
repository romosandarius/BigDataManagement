package dk.itu.bdm.mailreader;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

public class MailAcc {
	private final String subRegEx = "RE:? |Re:? |Sv:? |Fwd:? |SV:? |VS:? ";
	private final String imapAdress, account, pass;
	private Date firstMail, lastMail;
	private int mailsTotal, mailsSent;
	private HashMap<String, Integer> mailsPerAdress;
	private ConcurrentHashMap<String, ArrayList<Message>> conversations;
	private ArrayList<Message> tmp;

	public void addEmailToConversation(Message msg) throws MessagingException,
			NullPointerException {
		String cleanSub = msg.getSubject().replaceAll(subRegEx, "");
		if (conversations.containsKey(cleanSub)) {
			tmp = conversations.get(cleanSub);
			tmp.add(msg);
			// System.out.println(cleanSub+"\t"+tmp.size());
			conversations.put(cleanSub, tmp);
		} else {
			tmp = new ArrayList<Message>();
			tmp.add(msg);
			conversations.put(cleanSub, tmp);
		}

	}

	public ConcurrentHashMap<String, ArrayList<Message>> cleanConversations() {
		
		conversations.forEach((k, v) -> {
			if (v.size() < 2) {
				conversations.remove(k);
				// System.out.println("removed from conversations:" + k);
			}
		});
		 conversations= cleanOneWayCOnversations(conversations);
		return conversations;
	}

	private ConcurrentHashMap<String, ArrayList<Message>> cleanOneWayCOnversations(
			ConcurrentHashMap<String, ArrayList<Message>> conversations2) {
		System.out.println(conversations2.size());
		Stream<ArrayList<Message>> trueConversations = conversations2
				.values()
				.stream()
				.filter(m -> {
					return m.stream()
							.filter( 
									message -> {
										return
										message.getFrom().stream().filter(from -> from.toString()
											.equalsIgnoreCase(account).findFirst()
											.isPresent());
											});
				});
System.out.println(trueConversations.count());
System.out.println(conversations2.size());
return conversations2;
	}

	public HashMap<String, Integer> getMailsPerAdress() {
		return mailsPerAdress;
	}

	public void setMailsPerAdress(HashMap<String, Integer> collaborations) {
		// TODO: IMplement merging functionality
		this.mailsPerAdress = collaborations;
	}

	public Date getFirstMail() {
		return firstMail;
	}

	public void setFirstMail(Date firstMail) {
		if (this.firstMail == null || this.firstMail.after(firstMail)) {
			this.firstMail = firstMail;
		}
	}

	public Date getLastMail() {
		return lastMail;
	}

	public void setLastMail(Date lastMail) {
		if (this.lastMail == null || this.lastMail.before(lastMail)) {
			this.lastMail = lastMail;
		}
	}

	public int getMailsTotal() {
		return mailsTotal;
	}

	public void setMailsTotal(int mailsTotal) {
		this.mailsTotal = mailsTotal;
	}

	public int getMailsSent() {
		return mailsSent;
	}

	public void setMailsSent(int mailsSent) {
		this.mailsSent = mailsSent;
	}

	public String getImapAdress() {
		return imapAdress;
	}

	public String getAccount() {
		return account;
	}

	public String getPass() {
		return pass;
	}

	public MailAcc(String imapAdress, String account, String pass) {
		super();
		this.imapAdress = imapAdress;
		this.account = account;
		this.pass = pass;
		mailsPerAdress = new HashMap<String, Integer>();
		conversations = new ConcurrentHashMap<String, ArrayList<Message>>();
	}

	@Override
	public String toString() {
		return "MailAcc [ imapAdress=" + imapAdress + ", account=" + account
				+ ", pass=" + pass + ", firstMail=" + firstMail + ", lastMail="
				+ lastMail + ", mailsTotal=" + mailsTotal + ", mailsSent="
				+ mailsSent + ", mailsPerAdress=" + mailsPerAdress
				+ ", conversations=" + conversations.size() + "]";
	}
}
