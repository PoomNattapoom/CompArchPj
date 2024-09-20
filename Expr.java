import java.util.Map;

public interface Expr {
  long eval(Map<String, Long> bindings) throws EvalError;
}

interface Stmt {
  void eval(Map<String, Long> bindings) throws Exception;
}

