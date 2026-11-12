package ds;


public class MyStack {

    private String[] data;
    private int top;
    private int capacity;

    public MyStack() {
        capacity = 10;
        data = new String[capacity];
        top = -1;
    }

    public MyStack(int initialCapacity) {
        capacity = (initialCapacity > 0) ? initialCapacity : 10;
        data = new String[capacity];
        top = -1;
    }

    public void push(String item) {

        if (top == capacity - 1) {
            resize();
        }

        data[++top] = item;

    }


    public String pop() {

        if (isEmpty()) {
            throw new RuntimeException("Stack is empty. Cannot pop.");
        }

        String item = data[top];
        data[top] = null;
        top--;

        return item;

    }


    public String peek() {

        if (isEmpty()) {
            throw new RuntimeException("Stack is empty. Cannot peek.");
        }

        return data[top];

    }

    public boolean isEmpty() {
        return top == -1;
    }

    public int size() {
        return top + 1;
    }


    private void resize() {

        capacity *= 2;
        String[] newData = new String[capacity];

        System.arraycopy(data, 0, newData, 0, data.length);

        data = newData;

    }

}