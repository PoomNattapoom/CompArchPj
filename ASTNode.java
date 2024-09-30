import java.util.List;
import java.util.ArrayList;

// Base class for all AST nodes
abstract class ASTNode {
    public abstract void accept(NodeVisitor visitor);

    // Add a method to print the structure of the AST node
    @Override
    public abstract String toString();
}

class FillNode extends ASTNode {
    private LabelNode name;
    private NumberNode value;

    public FillNode(LabelNode name, NumberNode value) {
        this.name = name;
        this.value = value;
    }

    public LabelNode getName() {
        return name;
    }

    public NumberNode getValue() {
        return value;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Fill: " + name.toString() + ", " + value.toString();
    }

}

// AST Node for an instruction (e.g., ADD, BEQ)
class InstructionNode extends ASTNode {
    private String instruction;
    private List<ASTNode> operands; // Could be registers, numbers, or labels

    public InstructionNode(String instruction) {
        this.instruction = instruction;
        this.operands = new ArrayList<>();
    }

    public String getInstruction() {
        return instruction;
    }

    public List<ASTNode> getOperands() {
        return operands;
    }

    public void addOperand(ASTNode operand) {
        operands.add(operand);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Instruction: ").append(instruction).append(", Operands: [");
        for (int i = 0; i < operands.size(); i++) {
            sb.append(operands.get(i).toString());
            if (i < operands.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}

// AST Node for a register (e.g., x0, x1)
class RegisterNode extends ASTNode {
    private String register;

    public RegisterNode(String register) {
        this.register = register;
    }

    public String getRegister() {
        return register;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Register: " + register;
    }
}

// AST Node for a label (e.g., LOOP, EXIT)
class LabelNode extends ASTNode {
    private String label;

    public LabelNode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Label: " + label;
    }
}

// AST Node for an immediate value or number
class NumberNode extends ASTNode {
    private int number;

    public NumberNode(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Number: " + number;
    }
}

// Visitor interface for traversing AST nodes (optional for further steps)
interface NodeVisitor {
    void visit(InstructionNode node);

    void visit(RegisterNode node);

    void visit(LabelNode node);

    void visit(NumberNode node);

    void visit(FillNode node);
}
