package dk.itu.bdm.mailreader;

import java.util.*;

import javax.mail.Message;
import javax.mail.MessagingException;

public class MailAcc {
	private final String subRegEx = "RE:? |Re:? |Sv:? |Fwd:? |SV:? |VS:? ";
	private final String imapAdress, account, pass;
	private Date firstMail, lastMail;
	private int mailsTotal, mailsSent;
	private HashMap<String, Integer> mailsPerAdress;
	private HashMap<String, ArrayList<Message>> conversations;
	private HashSet<String> conversationSubjects;
	private ArrayList<Message> tmp;
	private String sendMailFolder;

	public MailAcc(String imapAdress, String account, String pass) {
		super();
		this.imapAdress = imapAdress;
		this.account = account;
		this.pass = pass;
		mailsPerAdress = new HashMap<String, Integer>();
		conversations = new HashMap<String, ArrayList<Message>>();
		conversationSubjects = new HashSet<String>();
	}

	public void putEmail(Message msg, boolean isSentMail)
			throws MessagingException, NullPointerException,IndexOutOfBoundsException {
		/*
		 * Do something with the mails per address, and the conversations
		 * hashmap
		 */
		System.err.print("'");
		String cleanSub = msg.getSubject().replaceAll(subRegEx, "");
		// If the mail is one i have sent
		if (isSentMail) {
			conversationSubjects.add(cleanSub);
		}
		// put all the mails in the hashmap
		tmp = conversations.getOrDefault(cleanSub, new ArrayList<Message>());
		tmp.add(msg);
		conversations.put(cleanSub, tmp);
		String sender = msg.getFrom()[0].toString();
		mailsPerAdress.put(sender, mailsPerAdress.getOrDefault(sender, 0) + 1);
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

	@Override
	public String toString() {
		return "MailAcc [ imapAdress=" + imapAdress + ", account=" + account
				+ ", pass=" + pass.hashCode() + ", firstMail=" + firstMail
				+ ", lastMail=" + lastMail + ", mailsTotal=" + mailsTotal
				+ ", mailsSent=" + mailsSent + ", mailsPerAdress="
				+ mailsPerAdress + ", conversations=" + conversations.size()
				+ "]";
	}

	public void setSendMailFolderName(String fullName) {
		sendMailFolder = fullName;

	}

	public String getSentFolderName() {
		return sendMailFolder;
	}

	public HashMap<String, ArrayList<Message>> cleanConversation() {
		System.out.println("cleaning conversations hashMap size before:"+conversations.size());
		for (String string : conversations.keySet()) {
			if(!conversationSubjects.contains(string))
				conversations.remove(string);
			System.out.print(".");
		}
		System.out.println("after: "+conversations.size());
		return conversations;
	}
	private int calculateMailsInConversations(){
		int i=0;
		Collection<ArrayList<Message>> tmp =conversations.values();
		for (ArrayList<Message> arrayList : tmp) {
			i+=arrayList.size();
		}
		return i;
	}

	private void printConversations() {
		conversations.forEach((k,v)->{
			System.out.println("subject: "+k+"\t lenght:"+v.size());
		});
	}
	private void printConversationInterval() {
		conversations.forEach((k,v)->{
			v.sort(new MessageComparator());
			for (Message message : v) {
				//TODO: conversationInterval here
			}
		});
	}
	public void printStatistics() {
		StringBuilder sb = new StringBuilder();
		String nl="\n";
		sb.append("Total mails: "+mailsTotal+nl);
		sb.append("Total mails sent: "+mailsSent+nl);
		sb.append("Conversations: "+conversations.size()+nl);
		sb.append("Mails belonging in a conversation:" + calculateMailsInConversations());
		sb.append("each mail conversation: "+nl);
		printConversations();
		printConversationInterval();
	}


}
