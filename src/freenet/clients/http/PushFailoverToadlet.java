package freenet.clients.http;

import java.io.IOException;
import java.net.URI;

import freenet.client.HighLevelSimpleClient;
import freenet.client.async.ClientGetter;
import freenet.clients.http.updateableelements.UpdaterConstants;
import freenet.support.Logger;
import freenet.support.api.HTTPRequest;

/** A toadlet that the client can use for push failover. It requires the requestId and originalRequestId parameter. */
public class PushFailoverToadlet extends Toadlet {

	private static volatile boolean logMINOR;
	
	static {
		Logger.registerClass(PushFailoverToadlet.class);
	}
	
	protected PushFailoverToadlet(HighLevelSimpleClient client) {
		super(client);
	}

	@Override
	public void handleGet(URI uri, HTTPRequest req, ToadletContext ctx) throws ToadletContextClosedException, IOException, RedirectException {
		String requestId = req.getParam("requestId");
		String originalRequestId = req.getParam("originalRequestId");
		boolean result = ((SimpleToadletServer) ctx.getContainer()).pushDataManager.failover(originalRequestId, requestId);
		if(logMINOR){
			Logger.minor(this,"Failover from:"+originalRequestId+" to:"+requestId+" with result:"+result);
		}
		writeHTMLReply(ctx, 200, "OK", result ? UpdaterConstants.SUCCESS : UpdaterConstants.FAILURE);
	}

	@Override
	public String path() {
		return UpdaterConstants.failoverPath;
	}

	@Override
	public String supportedMethods() {
		return "GET";
	}

}
