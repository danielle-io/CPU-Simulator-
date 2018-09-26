
public class ObjectQueue {
    private Object[] item;
    private int front;
    private int rear;
    private int count;

    public ObjectQueue(){
        item = new Object[1];
        front = 0;
        rear = -1;
        count = 0;
    }

    /**
     * Determines if the queue is empty or contains elements
     *
     * @return a boolean value indicating whether or not the queue is empty
     */
    public boolean isEmpty(){
        return count == 0;
    }

    /**
     * Determines if the queue has reached its maximum capacity for holding elements
     *
     * @return a boolean value indicating whether or not the queue is full
     */
    public boolean isFull(){
        return count == item.length;
    }

    public void clear(){
        item = new Object[1];
        front = 0;
        rear = -1;
        count = 0;
    }

    /**
     * Inserts an object into the rear of the queue
     *
     * @param o an object that will be added to the rear of the queue
     */
    public void insert(Object o){
        if (isFull())
            resize(2 * item.length);
        rear = (rear + 1) % item.length;
        item[rear] = o;
        ++count;
    }

    /**
     * Deletes an object from the front of the queue and returns its value
     *
     * @return the object that is removed from the front of the queue
     */
    public Object remove(){
        if (isEmpty()){
            System.out.println("Queue Underflow");
            System.exit(1);
        }
        Object temp = item[front];
        item[front] = null;
        front = (front +1) % item.length;
        --count;
        if (count == item.length/4 && item.length != 1)
            resize(item.length/2);
        return temp;
    }

    /**
     * Returns the front element of the queue without deleting the element from the queue
     *
     * @return the front element of the queue as an object
     */
    public Object query() {
        if (isEmpty()){
            System.out.println("Queue Underflow");
            System.exit(1);
        }
        return item[front];
    }

    /**
     * Resizes the queue
     *
     * @param size an int that notes the size of the queue
     */
    private void resize(int size){
        Object[] temp = new Object[size];
        for (int i = 0; i < count; i++){
            temp [i] = item[front];
            front = (front + 1) % item.length;
        }
        front = 0;
        rear = count - 1;
        item = temp;
    }
}
