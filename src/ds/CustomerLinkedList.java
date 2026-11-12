package ds;

import java2.Customer;

public class CustomerLinkedList {

    private class Node {

        Customer data;
        Node next;

        public Node(Customer data) {

            this.data = data;
            this.next = null;

        }

    }

    private Node head;

    public CustomerLinkedList() {

        head = null;

    }


    public void add(Customer customer) {

        Node newNode = new Node(customer);

        if (head == null) {

            head = newNode;
            return;

        }

        Node temp = head;

        while (temp.next != null) {

            temp = temp.next;

        }

        temp.next = newNode;

    }


    public void display() {

        if (head == null) {

            System.out.println("No Customers Found.");
            return;

        }

        Customer.printHeader();

        Node temp = head;

        while (temp != null) {

            temp.data.display();
            temp = temp.next;

        }

        Customer.printFooter();

    }


    public Customer searchById(int id) {

        Node temp = head;

        while (temp != null) {

            if (temp.data.getCustomerId() == id) {

                return temp.data;

            }

            temp = temp.next;

        }

        return null;

    }

    public boolean searchByName(String name) {

        Node temp = head;

        boolean found = false;

        while (temp != null) {

            if (temp.data.getName().equalsIgnoreCase(name)) {

                if (!found) {
                    Customer.printHeader();
                }

                temp.data.display();
                found = true;

            }

            temp = temp.next;

        }

        if (found) {
            Customer.printFooter();
        }

        return found;

    }

    public boolean delete(int id) {

        if (head == null)

            return false;

        if (head.data.getCustomerId() == id) {

            head = head.next;
            return true;

        }

        Node current = head;

        while (current.next != null) {

            if (current.next.data.getCustomerId() == id) {

                current.next = current.next.next;
                return true;

            }

            current = current.next;

        }

        return false;

    }


    public boolean isDuplicateId(int id) {

        return searchById(id) != null;

    }


    public boolean isEmpty() {

        return head == null;

    }
}