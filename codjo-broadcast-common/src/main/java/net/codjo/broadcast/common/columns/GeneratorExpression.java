/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.columns;
import net.codjo.expression.Expression;
import net.codjo.expression.ExpressionException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
/**
 * Gestionnaire d'expression pour l'export.
 *
 * @author $Author: galaber $
 * @version $Revision: 1.2 $
 */
class GeneratorExpression {
    private AbstractFileColumnGenerator generator;
    private Expression expression;


    GeneratorExpression(String expressionString, int inputSqlType, FunctionHolder funcs) {
        Expression.Variable[] inputs = new Expression.Variable[1];
        inputs[0] =
              new Expression.Variable(Expression.DEFAULT_STRING_VALUE, inputSqlType);
        FunctionHolder[] funcHolders = new FunctionHolder[2];
        funcHolders[0] = funcs;
        funcHolders[1] = new GeneratorFunctions();
        String[] st = {Expression.DEFAULT_STRING_NULL_VALUE};
        expression =
              new Expression(expressionString, inputs, funcHolders, Types.VARCHAR, st);
    }


    GeneratorExpression(String expressionString, int inputSqlType) {
        expression =
              new Expression(expressionString, inputSqlType, new GeneratorFunctions(),
                             Types.VARCHAR);
    }


    public String computeToString(Object value) throws GenerationException {
        try {
            Object result = expression.compute(value);
            if (result == null) {
                return "";
            }
            else {
                return result.toString();
            }
        }
        catch (ExpressionException ex) {
            throw new GenerationException("Evaluation de l'expression impossible :"
                                          + expression, ex);
        }
    }


    public void init(AbstractFileColumnGenerator columnGenerator) {
        this.generator = columnGenerator;
    }


    /**
     * Transfert l'appel de formattage sur le generateur.
     *
     * @author $Author: galaber $
     * @version $Revision: 1.2 $
     */
    public class GeneratorFunctions implements FunctionHolder {
        public List<String> getAllFunctions() {
            List<String> allFunction = new ArrayList<String>();
            allFunction.add("outil.format(value)");
            return allFunction;
        }


        public String getName() {
            return "outil";
        }


        public String format(Object obj) {
            return generator.format(obj);
        }
    }
}
