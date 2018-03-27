/*
 * generated by Xtext
 */
package msi.gama.lang.gaml.parser.antlr;

import com.google.inject.Inject;
import msi.gama.lang.gaml.parser.antlr.internal.InternalGamlParser;
import msi.gama.lang.gaml.services.GamlGrammarAccess;
import org.eclipse.xtext.parser.antlr.AbstractAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;

public class GamlParser extends AbstractAntlrParser {

	@Inject
	private GamlGrammarAccess grammarAccess;

	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT");
	}
	

	@Override
	protected InternalGamlParser createParser(XtextTokenStream stream) {
		return new InternalGamlParser(stream, getGrammarAccess());
	}

	@Override 
	protected String getDefaultRuleName() {
		return "Entry";
	}

	public GamlGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}

	public void setGrammarAccess(GamlGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
}
