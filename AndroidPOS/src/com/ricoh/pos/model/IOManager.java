package com.ricoh.pos.model;

import java.io.BufferedReader;

import android.content.res.AssetManager;

/**
 * This class manage I/O related database
 * @author Takuya Mizuhara
 *
 */
public interface IOManager {
	/**
	 * Import csv file from assets folder.
	 * @param assetManager asset file accessor
	 * @return buffered records imported from csv file 
	 */
	public abstract BufferedReader importCSVfromAssets(AssetManager assetManager);

	/**
	 * Insert all record into database.
	 * @param bufferReader buffered records imported from csv file
	 */
	public abstract void insertRecords(BufferedReader bufferReader);

	/**
	 * Search all record from database.
	 * @return search results
	 */
	public String[] searchAlldata();
	
	/**
	 * Search a record from database by product code
	 * @param product code connected with a record
	 * @return search result
	 */
	public abstract String searchByCode(String code);
}