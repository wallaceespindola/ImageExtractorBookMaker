package ImageExtractorBookMaker.BookMaker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Hello world!
 *
 */
public class App {
	
	private String cookies;
	private HttpClient client = HttpClientBuilder.create().build();
	private final String USER_AGENT = "Mozilla/5.0";
	
	public static void main( String[] args ) throws Exception {
		
        System.out.println( "Book Maker PDF!" );
        
        
        //String url = "http://leitorestacio.digitalpages.com.br/#/login";
        String url = "http://leitorestacio.digitalpages.com.br/#/edition/8309?page=18&section=1";
        
        String urlDados = "http://leitorestacio.digitalpages.com.br/#/edition/8309?page=18&section=1";
        
        String username = "201402244657";
        String password = "wjXpto2627";
        
        App http = new App();
        
		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());

		String page = http.GetPageContent(url);
		
		//System.out.println(page);

		List<NameValuePair> postParams = http.getFormParams(page, username, password);

		http.sendPost(url, postParams);

		String result = http.GetPageContent(urlDados);
		System.out.println(result);

		System.out.println("DONE!!!");
		
		
		
        
        int statusCode = http.autenticarSite(url, username, password);
        
        if (HttpStatus.SC_OK == statusCode) {
        	
        	statusCode = http.coletarDados();
        	
        	if (HttpStatus.SC_OK == statusCode) {
        		http.montarEbook();
        	} else {
        		System.out.println("ERRO AO COLETAR DADOS!");
        	}
        	
        } else {
    		System.out.println("ERRO AO COLETAR AUTENTICAR SITE!");
    	}
        
    }

	private int autenticarSite(String url, String user, String pass) throws Exception {
		
		HttpGet request = new HttpGet(url);

//		request.setHeader("User-Agent", USER_AGENT);
//		request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//		request.setHeader("Accept-Language", "en-US,en;q=0.5");

		System.out.println("\nSending 'GET' request to URL : " + url);

		HttpResponse response = client.execute(request);
		
		int responseCode = response.getStatusLine().getStatusCode();
		
		return responseCode;
	}
	
	private int coletarDados() {
		
				
		return 0;
	}
	
	private void montarEbook() {
		
	}
	
	private String GetPageContent(String url) throws Exception {

		HttpGet request = new HttpGet(url);

		request.setHeader("User-Agent", USER_AGENT);
		request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.setHeader("Accept-Language", "en-US,en;q=0.5");

		System.out.println("\nSending 'GET' request to URL : " + url);

		HttpResponse response = client.execute(request);
		int responseCode = response.getStatusLine().getStatusCode();

		System.out.println("Response Code: " + responseCode + ((responseCode==200)? " - OK" : "") );

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		// set cookies
		setCookies(response.getFirstHeader("Set-Cookie") == null ? "" : response.getFirstHeader("Set-Cookie").toString());

		return result.toString();

	}
	
	private void sendPost(String url, List<NameValuePair> postParams) throws Exception {

		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("Host", "accounts.google.com");
		post.setHeader("User-Agent", USER_AGENT);
		post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setHeader("Accept-Language", "en-US,en;q=0.5");
		post.setHeader("Cookie", getCookies());
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Referer", "https://accounts.google.com/ServiceLoginAuth");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		post.setEntity(new UrlEncodedFormEntity(postParams));

		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postParams);
		
		HttpResponse response = client.execute(post);

		int responseCode = response.getStatusLine().getStatusCode();

		System.out.println("Response Code: " + responseCode + ((responseCode==200)? " - OK" : "") );
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		System.out.println(result.toString());

	}
	
	public List<NameValuePair> getFormParams(String html, String username, String password) throws Exception {

		System.out.println("Extracting form's data...");

		//Jsoup.connect("www.google.com");
		Document doc = Jsoup.parse(html);

		// Google form id
		//Element loginform = doc.getElementsByTag("form");
		Elements inputElements = doc.getAllElements();

		List<NameValuePair> paramList = new ArrayList<NameValuePair>();

		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");
			String id = inputElement.id();

			if (id.equals("identification"))
				value = username;
			else if ( id.equals("password") )
				value = password;

			paramList.add(new BasicNameValuePair(key, value));

		}

		return paramList;
	}
	
	public String getCookies() {
		return cookies;
	}

	public void setCookies(String cookies) {
		this.cookies = cookies;
	}
}
