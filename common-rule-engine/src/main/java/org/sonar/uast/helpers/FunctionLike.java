package org.sonar.uast.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.CheckForNull;
import org.sonar.uast.UastNode;

public class FunctionLike {

  public static final UastNode.Kind KIND = UastNode.Kind.FUNCTION;
  private final UastNode node;
  private final UastNode block;

  private FunctionLike(UastNode node, UastNode block) {
    this.node = node;
    this.block = block;
  }

  @CheckForNull
  public static FunctionLike from(UastNode node) {
    if (node.kinds.contains(KIND)) {
      return node.getChild(UastNode.Kind.BLOCK).map(block -> new FunctionLike(node, block)).orElse(null);
    }
    return null;
  }

  public UastNode node() {
    return node;
  }

  public UastNode body() {
    return block;
  }

  public List<UastNode> parameters() {
    List<UastNode> parameters = node.getChildren(UastNode.Kind.PARAMETER);
    if (!parameters.isEmpty()) {
      return parameters;
    }
    return nestedParameters();
  }

  private List<UastNode> nestedParameters() {
    List<UastNode> parameters = new ArrayList<>();
    node.children.stream()
      .filter(child -> !child.kinds.contains(UastNode.Kind.BLOCK))
      .forEach(child -> child.getDescendants(UastNode.Kind.PARAMETER, parameters::add));
    return Collections.unmodifiableList(parameters);
  }
}
