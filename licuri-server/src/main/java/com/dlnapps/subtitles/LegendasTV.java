package com.dlnapps.subtitles;

import static java.net.URLEncoder.encode;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.dlnapps.http.client.HttpClient;
import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;

public class LegendasTV {

    private static final String USER_AGENT = "Mozilla/5.0";

    /**
     * Página para a busca das legendas Link é montado através da combinação
     * $resource/%1/%2/%3 onde: %1 = Termo da busca %2 = Código da linguagem %3
     * = Tipo de resultados esperados (todos, packs ou destaques apenas)
     */
    private static String resource = "http://legendas.tv/util/carrega_legendas_busca/%s/%s/%s";
    /**
     * Tradução para as diferentes linguagens que podem ser pesquisadas
     */
    protected static Map<String, String> languages;
    /**
     * Tipos de legendas para pesquisa
     */
    protected static Map<String, String> types;

    static {

	languages = new HashMap<String, String>();

	languages.put("Qualquer idioma", "-");
	languages.put("Português-BR", "1");
	languages.put("Inglês", "2");
	languages.put("Espanhol", "3");
	languages.put("Português-PT", "10");
	languages.put("Alemão", "5");
	languages.put("Árabe", "11");
	languages.put("Búlgaro", "15");
	languages.put("Checo", "12");
	languages.put("Chinês", "13");
	languages.put("Coreano", "14");
	languages.put("Dinamarquês", "7");
	languages.put("Francês", "4");
	languages.put("Italiano", "16");
	languages.put("Japonês", "6");
	languages.put("Norueguês", "8");
	languages.put("Polonês", "17");
	languages.put("Sueco", "9");

	types = new HashMap<String, String>();

	types.put("Todos", "-");
	types.put("Pack", "p");
	types.put("Destaque", "d");
    }

    /**
     * Efetua uma busca por legendas no site do legendas.tv
     * 
     * @param string
     *            O conteúdo da busca
     * @param string
     *            A linǵuagem da legenda
     * @return
     * @return array
     * @throws UnsupportedEncodingException
     * @throws Exception
     *             se o idioma for inválido
     * @todo Rolar a oaginação nos resultados da busca Retornar uma coleção de
     *       legendas, não um array, com métodos para ordenar por campos como
     *       por exemplo, highlight ou downloads
     */
    public static List<Subtitle> search(String search, String lang) throws UnsupportedEncodingException {

	return search(search, lang, null);
    }

    /**
     * Efetua uma busca por legendas no site do legendas.tv
     * 
     * @param string
     *            O conteúdo da busca
     * @param string
     *            A linǵuagem da legenda
     * @return
     * @return array
     * @throws UnsupportedEncodingException
     * @throws Exception
     *             se o idioma for inválido
     * @todo Rolar a oaginação nos resultados da busca Retornar uma coleção de
     *       legendas, não um array, com métodos para ordenar por campos como
     *       por exemplo, highlight ou downloads
     */
    public static List<Subtitle> search(String search, String lang, String type) throws UnsupportedEncodingException {

	if (lang == null)
	    lang = "Qualquer idioma";
	if (type == null)
	    type = "Todos";

	if (!languages.containsKey(lang)) {
	    throw new IllegalArgumentException("Idioma inválido");
	}

	String link = String.format(resource, encode(search, "UTF-8"), languages.get(lang), types.get(type));

	String page = request(link, "GET");
	List<Subtitle> subtitles = parse(page);

	return subtitles;
    }

    /**
     * Efetua o parse de uma página de listagem de legendas
     * 
     * @param string
     * @return array Todas as legendas identificadas
     * @todo Centralizar o parse de outras páginas aqui também.
     */
    private static List<Subtitle> parse(String page) {

	List<Subtitle> subtitles = new ArrayList<Subtitle>();

	String regex = "div class=\"(.*?)\">.*?<a href=\"(.*?)\">(.*?)<.*?p class=\"data\">(\\d+?) downloads, nota (\\d+?), enviado por .*?>(.*?)<\\/a> em (.*?) <\\/p>.*?<.*?alt=\"(.*?)\".*?<\\/div>";

	Pattern pattern = Pattern.compile(regex);

	Matcher matcher = pattern.matcher(page);

	while (matcher.find()) {
	    subtitles.add(new Subtitle(matcher.group(2).split("/")[2], matcher.group(2), matcher.group(3), "destaque".equals(matcher.group(1))));
	}

	return subtitles;

    }

    /**
     * Loga um usuario junto ao legendas.tv
     * 
     * @param string
     *            $username Nome de usuário no legendas.tv
     * @param string
     *            $password Senha
     * @return booelan
     * @throws IOException
     * @throws ClientProtocolException
     * @throws Exception
     *             Em caso de problemas no login
     */
    public static boolean login(String username, String password) throws ClientProtocolException, IOException {

	String url = "http://legendas.tv/login";

	HttpPost post = new HttpPost(url);

	// add header
	post.setHeader("User-Agent", USER_AGENT);

	List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	urlParameters.add(new BasicNameValuePair("_method", "POST"));
	urlParameters.add(new BasicNameValuePair("data[User][username]", username));
	urlParameters.add(new BasicNameValuePair("data[User][password]", password));

	post.setEntity(new UrlEncodedFormEntity(urlParameters));

	HttpResponse response = HttpClient.executePost(post);
	System.out.println("\nSending 'POST' request to URL : " + url);
	System.out.println("Post parameters : " + post.getEntity());
	System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

	StringBuffer result = new StringBuffer();
	String line = "";
	while ((line = rd.readLine()) != null) {
	    result.append(line);
	}

	return result.toString().toUpperCase().contains("SAIR");
    }

    /**
     * Efetua uma requisição ao site do Legendas.TV
     * 
     * @param string
     * @param boolean Req. Ajax
     * @param array
     *            Query a ser enviada por post
     * @param string
     *            GET (default) ou POST
     * @return
     * @return array Array com o Conteúdo da página, info do curl e header
     * @throws Exception
     *             Se o curl não for bem sucedido
     */
    public static String request(String url, String method) {
	//
	// for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++) {
	// System.out.println(key + ":" + conn.getHeaderField(i));
	// }

	CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);

	CookieHandler.setDefault(cookieManager);

	// cookieManager.getCookieStore().notifyAll();
	try {
	    // get URL content
	    URLConnection conn = new URL(url).openConnection();

	    return IOUtils.toString(conn.getInputStream());

	} catch (MalformedURLException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;

    }

    /**
     * Efetua a requisição de um arquivo ao servidor
     * 
     * @param string
     * @return string Arquivo ou link para o arquivo
     * @throws IOException
     * @throws FileNotFoundException
     * @throws IllegalStateException
     */
    public static void download(Subtitle subtitle, String parentFolder) throws IllegalStateException, FileNotFoundException, IOException {

	if (!login("wbccj", "willbc192")) {

	    throw new RuntimeException("Não conesguiu logar piá");
	}

	String url = "http://legendas.tv/downloadarquivo/" + subtitle.getId();
	HttpGet get = new HttpGet(url);

	// add header
	get.setHeader("User-Agent", USER_AGENT);

	HttpResponse response = HttpClient.executeGet(get, false);

	File file = new File(parentFolder, subtitle.getFileName());
	
	IOUtils.copy(response.getEntity().getContent(), new FileOutputStream(file));

	decompressFile(file);
    }

    private static void decompressFile(File file) {

	try {

	    Archive archive = new Archive(new FileVolumeManager(file));

	    FileHeader fh = archive.nextFileHeader();
	    while (fh != null) {

		if (!getExtension(fh.getFileNameString().trim()).equals("srt")) {
		    fh = archive.nextFileHeader();
		    continue;
		}

		File out = new File(file.getParent(), fh.getFileNameString().trim());

		FileOutputStream os = new FileOutputStream(out);
		archive.extractFile(fh, os);
		os.close();
		fh = archive.nextFileHeader();
	    }

	    closeQuietly(archive);

	} catch (RarException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static class Subtitle {

	private String id;
	private String link;
	private String fileName;
	private boolean highlight;

	public Subtitle(String id, String link, String fileName, boolean highlight) {
	    this.id = id;
	    this.link = link;
	    this.fileName = fileName;
	    this.highlight = highlight;
	}

	public String getLink() {
	    return link;
	}

	public String getFileName() {
	    return fileName;
	}

	public boolean isHighlight() {
	    return highlight;
	}

	public String getId() {
	    return id;
	}

	@Override
	public String toString() {
	    return "Subtitle [id=" + id + ", link=" + link + ", fileName=" + fileName + ", highlight=" + highlight + "]";
	}

    }
}
