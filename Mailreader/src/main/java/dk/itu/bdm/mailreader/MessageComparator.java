package dk.itu.bdm.mailreader;

import java.util.Comparator;

import javax.mail.Message;
import javax.mail.MessagingException;

public class MessageComparator implements Comparator<Message> {
@Override
public int compare(Message o1, Message o2) {
	
	try {
		return o1.getSentDate().compareTo(o2.getSentDate());
	} catch (MessagingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return 0;
	}
}
}
