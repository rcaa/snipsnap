package com.neotis.snip;

/**
 * Wraps the SnipTokenizer into an analyzer
 *
 * @author stephan
 * @version $Id$
 **/
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import java.io.Reader;

public final class SnipAnalyzer extends Analyzer {

  public final TokenStream tokenStream(Reader reader) {
    return new SnipTokenizer(reader);
  }
}