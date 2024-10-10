import java.util.List;
import java.util.ArrayList;

// Base class for all AST nodes
abstract class ASTNode {
    public abstract void accept(NodeVisitor visitor);

    // Add a method to print the structure of the AST node
    @Override
    public abstract String toString();

    public abstract String getValue();

    public abstract void firstAccept(NodeVisitor visitor);
}

// AST Node for an instruction (e.g., ADD, BEQ)
class InstructionNode extends ASTNode {
    private String instruction;
    private List<ASTNode> operands; // Could be registers, numbers, or labels

    public InstructionNode(String instruction) {
        this.instruction = instruction;
        this.operands = new ArrayList<>();
    }

    public InstructionNode(String instruction, ASTNode operand1, ASTNode operand2) {
        this.instruction = instruction;
        this.operands = new ArrayList<>();
        this.operands.add(operand1);
        this.operands.add(operand2);
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
    public void firstAccept(NodeVisitor visitor) {
        visitor.firstVisit(this);
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

    @Override
    public String getValue() {
        return instruction;
    }
}

// AST Node for a register (e.g., x0, x1)
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
    public void firstAccept(NodeVisitor visitor) {

    }

    @Override
    public String toString() {
        return "Label: " + label;
        // return "";
    }

    @Override
    public String getValue() {
        return label;
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
    public void firstAccept(NodeVisitor visitor) {

    }

    @Override
    public String toString() {
        return "Number: " + number;
    }

    @Override
    public String getValue() {
        return Integer.toString(number);
    }
}

// Visitor interface for traversing AST nodes (optional for further steps)
interface NodeVisitor {
    void firstVisit(InstructionNode node);

    void visit(InstructionNode node);

    void visit(LabelNode node);

    void visit(NumberNode node);
}
