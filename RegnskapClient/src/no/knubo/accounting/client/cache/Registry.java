package no.knubo.accounting.client.cache;

public interface Registry {
	/**
	 * Checks if the cache/registry contains the given key. 
	 * @param key The key to check for.
	 * @return True if it's valid.
	 */
	boolean keyExists(String key);
}
