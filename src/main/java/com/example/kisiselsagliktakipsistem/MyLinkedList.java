package com.example.kisiselsagliktakipsistem;

public class MyLinkedList<T> {
    private class Node {
        T data;
        Node next;
        Node(T data) { this.data = data; }
    }
    private Node head;
    private int size = 0;

    public void add(T data) {
        Node newNode = new Node(data);
        if (head == null) head = newNode;
        else {
            Node temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = newNode;
        }
        size++;
    }

    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Node temp = head;
        for (int i = 0; i < index; i++) temp = temp.next;
        return temp.data;
    }

    public void removeLast() {
        if (head == null) return;
        if (head.next == null) {
            head = null;
        } else {
            Node temp = head;
            while (temp.next.next != null) temp = temp.next;
            temp.next = null;
        }
        size--;
    }

    public boolean remove(T data) {
        if (head == null) return false;

        if (head.data.equals(data)) {
            head = head.next;
            size--;
            return true;
        }

        Node temp = head;
        while (temp.next != null && !temp.next.data.equals(data)) {
            temp = temp.next;
        }

        if (temp.next == null) return false; // Element not found

        temp.next = temp.next.next;
        size--;
        return true;
    }

    public int size() { return size; }

    public void clear() {
        head = null;
        size = 0;
    }
}