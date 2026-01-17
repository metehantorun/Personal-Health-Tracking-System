package com.example.kisiselsagliktakipsistem;

public class MyQueue<T> {
    private class Node {
        T data;
        Node next;
        Node(T data) { this.data = data; }
    }
    private Node front, rear;
    private int size = 0;

    public void enqueue(T data) {
        Node newNode = new Node(data);
        if (rear == null) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    public T dequeue() {
        if (front == null) return null;
        T data = front.data;
        front = front.next;
        if (front == null) rear = null;
        size--;
        return data;
    }

    public T peek() {
        if (front == null) return null;
        return front.data;
    }

    public boolean isEmpty() { return front == null; }
    public int size() { return size; }
}