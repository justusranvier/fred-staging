package freenet.node.fcp;

import java.io.IOException;
import java.io.OutputStream;

import freenet.node.Node;
import freenet.support.Logger;
import freenet.support.SimpleFieldSet;
import freenet.support.api.BucketFactory;
import freenet.support.io.PersistentTempBucketFactory;

public abstract class FCPMessage {

	public void send(OutputStream os) throws IOException {
		SimpleFieldSet sfs = getFieldSet();
		sfs.setEndMarker(getEndString());
		String msg = sfs.toString();
		os.write((getName()+ '\n').getBytes("UTF-8"));
		os.write(msg.getBytes("UTF-8"));
		if(Logger.shouldLog(Logger.DEBUG, this)) {
			Logger.debug(this, "Outgoing FCP message:\n"+sfs.toString());
			Logger.debug(this, "Being handled by "+this);
		}
	}

	String getEndString() {
		return "EndMessage";
	}
	
	public abstract SimpleFieldSet getFieldSet();

	public abstract String getName();
	
	/**
	 * Create a message from a SimpleFieldSet, and the message's name, if possible. 
	 */
	public static FCPMessage create(String name, SimpleFieldSet fs, BucketFactory bfTemp, PersistentTempBucketFactory bfPersistent) throws MessageInvalidException {
		if(name.equals(AddPeer.name))
			return new AddPeer(fs);
		if(name.equals(ClientGetMessage.name))
			return new ClientGetMessage(fs);
		if(name.equals(ClientHelloMessage.name))
			return new ClientHelloMessage(fs);
		if(name.equals(ClientPutComplexDirMessage.name))
			return new ClientPutComplexDirMessage(fs, bfTemp, bfPersistent);
		if(name.equals(ClientPutDiskDirMessage.name))
			return new ClientPutDiskDirMessage(fs);
		if(name.equals(ClientPutMessage.name))
			return new ClientPutMessage(fs);
		if(name.equals(GenerateSSKMessage.name))
			return new GenerateSSKMessage(fs);
		if(name.equals(GetConfig.name))
			return new GetConfig(fs);
		if(name.equals(GetNode.name))
			return new GetNode(fs);
		if(name.equals(GetRequestStatusMessage.name))
			return new GetRequestStatusMessage(fs);
		if(name.equals(ListPeersMessage.name))
			return new ListPeersMessage(fs);
		if(name.equals(ListPeerNotesMessage.name))
			return new ListPeerNotesMessage(fs);
		if(name.equals(ListPersistentRequestsMessage.name))
			return new ListPersistentRequestsMessage(fs);
		if(name.equals(ModifyConfig.name))
			return new ModifyConfig(fs);
		if(name.equals(ModifyPeer.name))
			return new ModifyPeer(fs);
		if(name.equals(ModifyPeerNote.name))
			return new ModifyPeerNote(fs);
		if(name.equals(ModifyPersistentRequest.name))
			return new ModifyPersistentRequest(fs);
		if(name.equals(RemovePeer.name))
			return new RemovePeer(fs);
		if(name.equals(RemovePersistentRequest.name))
			return new RemovePersistentRequest(fs);
		if(name.equals(ShutdownMessage.name))
			return new ShutdownMessage();
		if(name.equals(SubscribeUSKMessage.name))
			return new SubscribeUSKMessage(fs);
		// Removed until security issues sorted out
//		if(name.equals(TestDDAMessage.name))
//			return new TestDDAMessage(fs);
		if(name.equals(WatchGlobal.name))
			return new WatchGlobal(fs);
		if(name.equals("Void"))
			return null;
		if(name.equals(NodeHelloMessage.name))
			return new NodeHelloMessage(fs);
		throw new MessageInvalidException(ProtocolErrorMessage.INVALID_MESSAGE, "Unknown message name "+name, null, false);
//		if(name.equals("ClientPut"))
//			return new ClientPutFCPMessage(fs);
		// TODO Auto-generated method stub
	}
	
	/**
	 * Create a message from a SimpleFieldSet, and the message's name, if possible. 
	 * Usefull for FCPClients
	 */
	public static FCPMessage create(String name, SimpleFieldSet fs) throws MessageInvalidException {
		return FCPMessage.create(name, fs, null, null);
	}

	/** Do whatever it is that we do with this type of message. 
	 * @throws MessageInvalidException */
	public abstract void run(FCPConnectionHandler handler, Node node) throws MessageInvalidException;

}
