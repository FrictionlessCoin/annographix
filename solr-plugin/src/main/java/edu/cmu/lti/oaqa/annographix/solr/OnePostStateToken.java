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
 *  
 *  The author re-used some of the code from org.apache.lucene.search.PhraseQuery,
 *  which was also licensed under the Apache License 2.0.
 *  
 */
package edu.cmu.lti.oaqa.annographix.solr;

import java.io.IOException;

import org.apache.lucene.index.DocsAndPositionsEnum;

/**
 * This a helper class to read token info from posting lists.
 * 
 * @author Leonid boytsov
 *
 */
public class OnePostStateToken extends OnePostStateBase {

  /**
    * @param posting    an already initialized posting list.
    */
  public OnePostStateToken(DocsAndPositionsEnum posting) {
    super(posting);
  }

  /**
   * Read next element {@link edu.cmu.lti.oaqa.annographix.solr.OnePostStateBase#readDocElements()}.
   */
  @Override
  protected void readDocElements() throws IOException {
    mQty = mPosting.freq();
    // Ensure we have enough space to store 
    extendElemInfo(mQty);
    for (int i = 0; i < mQty; ++i) {
      mPosting.nextPosition();
      ElemInfoData dt = mElemInfo[i];
      dt.mId = -1;
      dt.mParentId = -1;
      dt.mStartOffset = mPosting.startOffset();
      dt.mEndOffset = mPosting.endOffset();
    }
  }

}