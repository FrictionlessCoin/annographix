

/* First created by JCasGen Thu Oct 30 13:00:49 EDT 2014 */
package edu.cmu.lti.oaqa.annographix.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Part-Of-Speach (POS) tag.
 * Updated by JCasGen Thu Oct 30 13:00:49 EDT 2014
 * XML source: /home/leo/SourceTreeGit/annographix/core/src/main/resources/types/MainTypes.xml
 *  */
public class POS extends Annotation {
  /** 
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(POS.class);
  /** 
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /**   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   *  */
  protected POS() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   *  */
  public POS(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /**  */
  public POS(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /**  */  
  public POS(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
   modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: Tag

  /** getter for Tag - gets An actual POS-tag, e.g., NN or VP
   *  */
  public String getTag() {
    if (POS_Type.featOkTst && ((POS_Type)jcasType).casFeat_Tag == null)
      jcasType.jcas.throwFeatMissing("Tag", "edu.cmu.lti.oaqa.annographix.types.POS");
    return jcasType.ll_cas.ll_getStringValue(addr, ((POS_Type)jcasType).casFeatCode_Tag);}
    
  /** setter for Tag - sets An actual POS-tag, e.g., NN or VP 
   *  */
  public void setTag(String v) {
    if (POS_Type.featOkTst && ((POS_Type)jcasType).casFeat_Tag == null)
      jcasType.jcas.throwFeatMissing("Tag", "edu.cmu.lti.oaqa.annographix.types.POS");
    jcasType.ll_cas.ll_setStringValue(addr, ((POS_Type)jcasType).casFeatCode_Tag, v);}    
  }

    
