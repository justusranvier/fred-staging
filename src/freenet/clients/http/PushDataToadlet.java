package freenet.clients.http;

import java.io.IOException;
import java.net.URI;

import freenet.client.HighLevelSimpleClient;
import freenet.client.async.ClientGetter;
import freenet.clients.http.updateableelements.BaseUpdateableElement;
import freenet.clients.http.updateableelements.UpdaterConstants;
import freenet.support.Base64;
import freenet.support.Logger;
import freenet.support.api.HTTPRequest;

/** A toadlet that provides the current data of pushed elements. It requires the requestId and the elementId parameters. */
public class PushDataToadlet extends Toadlet {

	private static volatile boolean logMINOR;
	
	static {
		Logger.registerClass(PushDataToadlet.class);
	}
	
	/** The separator char that separates the response's parts. It must not be present at the BASE64 alphabet. */
	public static final String	SEPARATOR	= ":";

	protected PushDataToadlet(HighLevelSimpleClient client) {
		super(client);
	}

	@Override
	public void handleGet(URI uri, HTTPRequest req, ToadletContext ctx) throws ToadletContextClosedException, IOException, RedirectException {
		String requestId = req.getParam("requestId");
		String elementId = req.getParam("elementId");
		elementId = elementId.replace(" ", "+");// This is needed, because BASE64 has '+', but it is a HTML escape for ' '
		if(logMINOR){
			Logger.minor(this,"Getting data for element:"+elementId);
		}
		BaseUpdateableElement node = ((SimpleToadletServer) ctx.getContainer()).pushDataManager.getRenderedElement(requestId, elementId);
		if(logMINOR){
			Logger.minor(this,"Data got element:"+node.generateChildren());
		}
		writeHTMLReply(ctx, 200, "OK", UpdaterConstants.SUCCESS + ":" + Base64.encodeStandard(node.getUpdaterType().getBytes()) + ":" + Base64.encodeStandard(node.generateChildren().getBytes()));
	}

	@Override
	public String path() {
		return UpdaterConstants.dataPath;
	}

	@Override
	public String supportedMethods() {
		return "GET";
	}

}
