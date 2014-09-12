package dk.itu.bdm.mailreader;

import java.util.Date;

public class MailAcc {
private String imapAdress,account,pass;
private Date firstMail,lastMail;
private int mailsTotal, mailsSent;

public Date getFirstMail() {
	return firstMail;
}

public void setFirstMail(Date firstMail) {
	if (this.firstMail==null||this.firstMail.after(firstMail)){
	this.firstMail = firstMail;
	}
}

public Date getLastMail() {
	return lastMail;
}

public void setLastMail(Date lastMail) {
	if(this.lastMail==null||this.lastMail.before(lastMail)){
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

public void setImapAdress(String imapAdress) {
	this.imapAdress = imapAdress;
}

public String getAccount() {
	return account;
}

public void setAccount(String account) {
	this.account = account;
}

public String getPass() {
	return pass;
}

public void setPass(String pass) {
	this.pass = pass;
}

public MailAcc(String imapAdress, String account, String pass) {
	super();
	this.imapAdress = imapAdress;
	this.account = account;
	this.pass = pass;
}

@Override
public String toString() {
	return "MailAcc [imapAdress=" + imapAdress + ", account=" + account
			+ ", pass=" + pass.hashCode() + ", firstMail=" + firstMail + ", lastMail="
			+ lastMail + ", mailsTotal=" + mailsTotal + ", mailsSent="
			+ mailsSent + "]";
}
}
