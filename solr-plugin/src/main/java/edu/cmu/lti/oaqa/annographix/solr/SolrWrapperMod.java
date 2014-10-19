/*
 *  Copyright 2014 Carnegie Mellon University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package edu.cmu.lti.oaqa.annographix.solr;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;

/**
 * This is a simplified and re-worked version of SolrWrapper from:
 * https://github.com/oaqa/solr-provider/ .
 *
 * <p>
 * It is different in at least two ways:
 * </p>
 * <ol> 
 *   <li> 
 *      It has a function submitXml, which allows one to pass arbitrary XML.
 *      This is useful for batch mode.
 *   </li>   
 *   <li>
 *     It doesn't support embedded Solr server.
 *   </li>
 *   <li> 
 *     It has an improved function getFieldText that can read text from  multiple-value text fields.
 *   </li> 
 * </ol>
 * 
 * @author Alkesh Patel 
 * @author Leonid Boytsov (modified, see the comment above)
 * 
 */
public final class SolrWrapperMod implements Closeable {
	private final SolrServer server;

	boolean embedded;

	public SolrWrapperMod(String serverUrl) throws Exception{
	  this.server=createSolrServer(serverUrl);
	  this.embedded = false;
	}

	private SolrServer createSolrServer(String url) throws Exception {
		SolrServer server = new HttpSolrServer(url);
		// server.ping();
		return server;
	}

	public SolrServer getServer() throws Exception {
		return server;
	}

	public SolrDocumentList runQuery(String q, int results)
			throws SolrServerException {
		SolrQuery query = new SolrQuery();
		query.setQuery(escapeQuery(q));
		query.setRows(results);
		query.setFields("*", "score");
		QueryResponse rsp = server.query(query, METHOD.POST);
		return rsp.getResults();
	}

	public SolrDocumentList runQuery(SolrQuery query, int results)
			throws SolrServerException {
		QueryResponse rsp = server.query(query);
		return rsp.getResults();
	}

	public SolrDocumentList runQuery(String q, List<String> fieldList,
			int results) throws SolrServerException {
		SolrQuery query = new SolrQuery();
		query.setQuery(escapeQuery(q));
		query.setRows(results);
		query.setFields(fieldList.toArray(new String[1]));
		QueryResponse rsp = server.query(query, METHOD.POST);
		return rsp.getResults();
	}

	public ArrayList<String> getFieldText(String id, String idField, String textField) 
							throws SolrServerException {
		String q = idField + ":" + id;
		SolrQuery query = new SolrQuery();
		query.setQuery(q);
		query.setFields(textField);
		QueryResponse rsp = server.query(query);

		ArrayList<String> docText = null;
		if (rsp.getResults().getNumFound() > 0) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Object o = rsp.getResults().get(0).getFieldValues(textField);
			if (o instanceof String) {
				docText = new ArrayList<String>();
				docText.add((String)o);
			} else {
				ArrayList<String> results = (ArrayList)o; 
				docText = results;
			}
		}
		return docText;
	}

	public String escapeQuery(String term) {
		term = term.replace('?', ' ');
		term = term.replace('[', ' ');
		term = term.replace(']', ' ');
		term = term.replace('/', ' ');
		term = term.replaceAll("\'", "");
		return term;
	}

	public void close() {
		if (embedded) {
			((EmbeddedSolrServer) server).shutdown();
		}
	}

	/**
    * Create a solr document for indexing from key-value (field name, field value) pairs.
    */
	public SolrInputDocument buildSolrDocument(HashMap<String, Object> hshMap)
			throws Exception {

		SolrInputDocument doc = new SolrInputDocument();

		Iterator<String> keys = hshMap.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Object value = hshMap.get(key);

			SolrInputField field = new SolrInputField(key);
			try {

				doc.addField(field.getName(), value, 1.0f);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return doc;

	}
	
	/**
    * Submit a command (or a batch of commands) in xml format.
    */ 
	public void submitXML(String docXML) throws Exception {
		DirectXmlRequest xmlreq = new DirectXmlRequest("/update", docXML);
		server.request(xmlreq);
	}
	
	/**
    * Converting SolrInputDocument into XML to post over HTTP for indexing.
    */
	public String convertSolrDocInXML(SolrInputDocument solrDoc)throws Exception{
		return ClientUtils.toXML(solrDoc);
	}
	
	/**
    * Indexing one document (in XML format).
    */
	public void indexDocument(String docXML) throws Exception {

		String xml = "<add>" + docXML + "</add>";
		DirectXmlRequest xmlreq = new DirectXmlRequest("/update", xml);
		server.request(xmlreq);
	}

	/**
    * Indexing one document (key-value pair format).
    */
	public void indexDocument(HashMap<String,Object>indexMap) throws Exception {
		SolrInputDocument solrDoc=this.buildSolrDocument(indexMap);
		String docXML=this.convertSolrDocInXML(solrDoc);
		String xml = "<add>" + docXML + "</add>";
		DirectXmlRequest xmlreq = new DirectXmlRequest("/update", xml);
		server.request(xmlreq);
	}

	
	/**
    * Issue a commit.
    */
	public void indexCommit() throws Exception {
		server.commit();
	}
	
	/**
    * Delete documents that satisfy a given query.
    */
	public void deleteDocumentByQuery(String query)throws Exception{
		
		server.deleteByQuery(query);
	}
}
