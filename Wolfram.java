package com.amazonaws.lambda.demo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;

public class Wolfram {

	private String appid = "8RHLET-2XAKLRPE2H";

	public String compute(String input) {
		// Use "pi" as the default query, or caller can supply it as the lone
		// command-line argument.

		String url = null;
		try {
			url = "api.wolframalpha.com/v1/spoken?appid=" + appid + "&i=" + URLEncoder.encode(input, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
		}

		// The WAEngine is a factory for creating WAQuery objects,
		// and it also used to perform those queries. You can set properties of
		// the WAEngine (such as the desired API output format types) that will
		// be inherited by all WAQuery objects created from it. Most
		// applications
		// will only need to crete one WAEngine object, which is used throughout
		// the life of the application.
		WAEngine engine = new WAEngine();

		// These properties will be set in all the WAQuery objects created from
		// this WAEngine.
		engine.setAppID(appid);
		engine.addFormat("plaintext");

		// Create the query.
		WAQuery query = engine.createQuery();

		// Set properties of the query.
		query.setInput(input);

		String output = "";
		try {
			// For educational purposes, print out the URL we are about to send:
			System.out.println("Query URL:");
			System.out.println(engine.toURL(query));
			System.out.println("");

			// This sends the URL to the Wolfram|Alpha server, gets the XML
			// result
			// and parses it into an object hierarchy held by the WAQueryResult
			// object.
			WAQueryResult queryResult = engine.performQuery(query);

			if (queryResult.isError()) {
				System.out.println("Query error");
				System.out.println("  error code: " + queryResult.getErrorCode());
				System.out.println("  error message: " + queryResult.getErrorMessage());
			} else if (!queryResult.isSuccess()) {
				System.out.println("Query was not understood; no results available.");
			} else {
				// Got a result.
				//System.out.println("Successful query. Pods follow:\n");
				for (WAPod pod : queryResult.getPods()) {
					if (!pod.isError()) {
						//System.out.println(pod.getTitle());
						// System.out.println("------------");
						for (WASubpod subpod : pod.getSubpods()) {
							for (Object element : subpod.getContents()) {
								if (element instanceof WAPlainText) {
									output = output + (((WAPlainText) element).getText()) + "\n";
								}
							}
						}
					}
				}
				// We ignored many other types of Wolfram|Alpha output, such as
				// warnings, assumptions, etc.
				// These can be obtained by methods of WAQueryResult or objects
				// deeper in the hierarchy.
			}
		} catch (WAException e) {
			e.printStackTrace();
		}
		return output;
	}
}
