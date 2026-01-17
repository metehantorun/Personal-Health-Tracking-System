package com.example.kisiselsagliktakipsistem;

public class MyStack<T> {
    private class Node {
        T data;
        Node next;
        Node(T data) { this.data = data; }
    }
    private Node top;
    private int size = 0;

    public void push(T data) {
        Node newNode = new Node(data);
        newNode.next = top;
        top = newNode;
        size++;
    }

    public T pop() {
        if (top == null) return null;
        T data = top.data;
        top = top.next;
        size--;
        return data;
    }

    public T peek() {
        if (top == null) return null;
        return top.data;
    }

    public boolean isEmpty() { return top == null; }
    public int size() { return size; }
}