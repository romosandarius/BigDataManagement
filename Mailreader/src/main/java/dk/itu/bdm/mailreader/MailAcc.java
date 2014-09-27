package dk.itu.bdm.mailreader;

import java.util.*;

import javax.mail.Message;
import javax.mail.MessagingException;import javax.mail.internet.InternetAddress;


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
		System.out.println();
//		conversations.keySet().forEach(k-> System.out.print(k));
		System.out.println();
//		System.out.println(conversationSubjects.toString());
		for (Iterator<String> iterator = conversations.keySet().iterator(); iterator
				.hasNext();) {
			String string = (String) iterator.next();
			if(!conversationSubjects.contains(string))
				iterator.remove();
			System.out.print(".");
			
		}
		System.out.println("\nafter: "+conversations.size());
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
	private void printConversationInterval() {
		System.out.println("\n\n");
		System.out.println("Mail threads(subject), and their intervals in seconds");
		conversations.forEach((k,v)->{
			v.sort(new MessageComparator());
			for (Iterator<Message> iterator = v.iterator(); iterator.hasNext();) {
				Message message = (Message) iterator.next();
				//If the
				try {
					if(!((InternetAddress)message.getFrom()[0]).getAddress().equalsIgnoreCase(account)){
						while (true&&iterator.hasNext()){
						Message myMessage=iterator.next();
						if(((InternetAddress)myMessage.getFrom()[0]).getAddress().equalsIgnoreCase(account)){
							System.out.println(k+","+((myMessage.getSentDate().getTime()-message.getSentDate().getTime())/1000));
//							System.out.println("there is "+((myMessage.getSentDate().getTime()-message.getSentDate().getTime())/60000)+" minutes between replies in subject "+k);
//							System.out.println("\t "+myMessage.getSentDate().toString()+"\t "+message.getSentDate().toString());
						break;
						}
						}
						}
				} catch (Exception e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
			}
			
		});
		System.out.println("\n\n");
	}
	public void printStatistics() {
		StringBuilder sb = new StringBuilder();
		String nl="\n";
		sb.append("Total mails: "+mailsTotal+nl);
		sb.append("Total mails sent: "+mailsSent+nl);
		sb.append("Conversations: "+conversations.size()+nl);
		sb.append("Mails belonging in a conversation:" + calculateMailsInConversations());
		System.out.println(sb.toString());
		printConversationInterval();
		System.out.println("\n\n");
		printMailThreadSize();
	}

	private void printMailThreadSize() {
		System.out.println("\n\n");
		System.out.println("Mail threads(subject), and their sizes");
		conversations.forEach((k,v)->{
			if(v.size()>1)
			System.out.println(k+","+v.size());
		});
		System.out.println("\n\n");
	}


}
