import java.util.HashMap;
import java.util.Random;

public class URLShortener {
    private HashMap<String, String> keyMap; // key-url map
    private HashMap<String, String> valueMap; // url-key map
    private String domain; // Custom domain
    private char[] myChars; // Character mapping
    private Random myRand; // Random generator
    private int keyLength; // Key length in URL

    // Default Constructor
    public URLShortener() {
        keyMap = new HashMap<>();
        valueMap = new HashMap<>();
        myRand = new Random();
        keyLength = 8;
        myChars = new char[62];
        for (int i = 0; i < 62; i++) {
            myChars[i] = (char) (i < 10 ? i + 48 : (i < 36 ? i + 55 : i + 61));
        }
        domain = "http://fkt.in";
    }

    // Constructor to define key length and domain
    public URLShortener(int length, String newDomain) {
        this();
        if (length > 0) {
            this.keyLength = length;
        }
        if (!newDomain.isEmpty()) {
            domain = sanitizeURL(newDomain);
        }
    }

    // Shorten URL
    public String shortenURL(String longURL) {
        if (validateURL(longURL)) {
            longURL = sanitizeURL(longURL);
            return domain + "/" + (valueMap.containsKey(longURL) ? valueMap.get(longURL) : getKey(longURL));
        }
        return "Invalid URL";
    }

    // Expand URL
    public String expandURL(String shortURL) {
        if (shortURL == null || !shortURL.startsWith(domain)) {
            return "Invalid short URL";
        }
        String key = shortURL.substring(domain.length() + 1);
        return keyMap.getOrDefault(key, "URL not found");
    }

    // Validate URL
    boolean validateURL(String url) {
        return url != null && !url.isEmpty() && (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("www."));
    }

    // Sanitize URL
    String sanitizeURL(String url) {
        if (url.startsWith("http://")) {
            url = url.substring(7);
        } else if (url.startsWith("https://")) {
            url = url.substring(8);
        } else if (url.startsWith("www.")) {
            url = url.substring(4);
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    // Get Key
    private String getKey(String longURL) {
        String key = generateKey();
        keyMap.put(key, longURL);
        valueMap.put(longURL, key);
        return key;
    }

    // Generate Key
    private String generateKey() {
        StringBuilder key;
        int attempts = 0;
        while (true) {
            attempts++;
            key = new StringBuilder();
            for (int i = 0; i < keyLength; i++) {
                key.append(myChars[myRand.nextInt(62)]);
            }
            if (!keyMap.containsKey(key.toString()) && !valueMap.containsKey(key.toString())) {
                break;
            }
            // Optional: break after too many attempts to avoid infinite loop
            if (attempts > 1000) {
                throw new RuntimeException("Failed to generate a unique key after multiple attempts.");
            }
        }
        return key.toString();
    }

    // Test the code
    public static void main(String[] args) {
        URLShortener u = new URLShortener(5, "http://www.tinyurl.com");
        String[] urls = {
            "www.google.com/", "www.google.com",
            "http://www.yahoo.com", "www.yahoo.com/",
            "www.amazon.com", "www.amazon.com/page1.php",
            "www.amazon.com/page2.php", "www.flipkart.in",
            "www.rediff.com", "www.techmeme.com",
            "www.techcrunch.com", "www.lifehacker.com",
            "www.icicibank.com"
        };

        for (String url : urls) {
            String shortUrl = u.shortenURL(url);
            System.out.println("URL: " + url + "\tTiny: " + shortUrl + "\tExpanded: " + u.expandURL(shortUrl));
        }
    }
}
