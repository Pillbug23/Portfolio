/* Generated By:JJTree: Do not edit this line. ASTAdditiveExpression.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package edu.berkeley.cs186.database.cli.parser;

public
class ASTAdditiveExpression extends SimpleNode {
  public ASTAdditiveExpression(int id) {
    super(id);
  }

  public ASTAdditiveExpression(RookieParser p, int id) {
    super(p, id);
  }

  /** Accept the visitor. **/
  public void jjtAccept(RookieParserVisitor visitor, Object data) {
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=fd174c487532a42f9b13589a4e4f3632 (do not edit this line) */
