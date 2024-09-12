//@formatter:off
/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.2.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package me.him188.ani.app.torrent.anitorrent.binding;

public class PeerInfoList extends java.util.AbstractList<peer_info_t> implements java.util.RandomAccess {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected PeerInfoList(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(PeerInfoList obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(PeerInfoList obj) {
    long ptr = 0;
    if (obj != null) {
      if (!obj.swigCMemOwn)
        throw new RuntimeException("Cannot release ownership as memory is not owned");
      ptr = obj.swigCPtr;
      obj.swigCMemOwn = false;
      obj.delete();
    }
    return ptr;
  }

  @SuppressWarnings({"deprecation", "removal"})
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        anitorrentJNI.delete_PeerInfoList(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public PeerInfoList(peer_info_t[] initialElements) {
    this();
    reserve(initialElements.length);

    for (peer_info_t element : initialElements) {
      add(element);
    }
  }

  public PeerInfoList(Iterable<peer_info_t> initialElements) {
    this();
    for (peer_info_t element : initialElements) {
      add(element);
    }
  }

  public peer_info_t get(int index) {
    return doGet(index);
  }

  public peer_info_t set(int index, peer_info_t e) {
    return doSet(index, e);
  }

  public boolean add(peer_info_t e) {
    modCount++;
    doAdd(e);
    return true;
  }

  public void add(int index, peer_info_t e) {
    modCount++;
    doAdd(index, e);
  }

  public peer_info_t remove(int index) {
    modCount++;
    return doRemove(index);
  }

  protected void removeRange(int fromIndex, int toIndex) {
    modCount++;
    doRemoveRange(fromIndex, toIndex);
  }

  public int size() {
    return doSize();
  }

  public int capacity() {
    return doCapacity();
  }

  public void reserve(int n) {
    doReserve(n);
  }

  public PeerInfoList() {
    this(anitorrentJNI.new_PeerInfoList__SWIG_0(), true);
  }

  public PeerInfoList(PeerInfoList other) {
    this(anitorrentJNI.new_PeerInfoList__SWIG_1(PeerInfoList.getCPtr(other), other), true);
  }

  public boolean isEmpty() {
    return anitorrentJNI.PeerInfoList_isEmpty(swigCPtr, this);
  }

  public void clear() {
    anitorrentJNI.PeerInfoList_clear(swigCPtr, this);
  }

  public PeerInfoList(int count, peer_info_t value) {
    this(anitorrentJNI.new_PeerInfoList__SWIG_2(count, peer_info_t.getCPtr(value), value), true);
  }

  private int doCapacity() {
    return anitorrentJNI.PeerInfoList_doCapacity(swigCPtr, this);
  }

  private void doReserve(int n) {
    anitorrentJNI.PeerInfoList_doReserve(swigCPtr, this, n);
  }

  private int doSize() {
    return anitorrentJNI.PeerInfoList_doSize(swigCPtr, this);
  }

  private void doAdd(peer_info_t x) {
    anitorrentJNI.PeerInfoList_doAdd__SWIG_0(swigCPtr, this, peer_info_t.getCPtr(x), x);
  }

  private void doAdd(int index, peer_info_t x) {
    anitorrentJNI.PeerInfoList_doAdd__SWIG_1(swigCPtr, this, index, peer_info_t.getCPtr(x), x);
  }

  private peer_info_t doRemove(int index) {
    return new peer_info_t(anitorrentJNI.PeerInfoList_doRemove(swigCPtr, this, index), true);
  }

  private peer_info_t doGet(int index) {
    return new peer_info_t(anitorrentJNI.PeerInfoList_doGet(swigCPtr, this, index), false);
  }

  private peer_info_t doSet(int index, peer_info_t val) {
    return new peer_info_t(anitorrentJNI.PeerInfoList_doSet(swigCPtr, this, index, peer_info_t.getCPtr(val), val), true);
  }

  private void doRemoveRange(int fromIndex, int toIndex) {
    anitorrentJNI.PeerInfoList_doRemoveRange(swigCPtr, this, fromIndex, toIndex);
  }

}

//@formatter:on