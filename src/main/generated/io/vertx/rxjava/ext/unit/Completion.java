/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.rxjava.ext.unit;

import java.util.Map;
import rx.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.rxjava.core.Future;

/**
 * A completion object that emits completion notifications either <i>succeeded</i> or <i>failed</i>.
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.unit.Completion original} non RX-ified interface using Vert.x codegen.
 */

public class Completion<T> {

  final io.vertx.ext.unit.Completion delegate;

  public Completion(io.vertx.ext.unit.Completion delegate) {
    this.delegate = delegate;
  }

  public Object getDelegate() {
    return delegate;
  }

  /**
   * Completes the future upon completion, otherwise fails it.
   * @param future the future to resolve
   */
  public void resolve(Future<T> future) { 
    delegate.resolve((io.vertx.core.Future<T>)future.getDelegate());
  }

  /**
   * @return true if this completion is completed
   * @return 
   */
  public boolean isCompleted() { 
    boolean ret = delegate.isCompleted();
    return ret;
  }

  /**
   * @return true if the this completion is completed succeeded
   * @return 
   */
  public boolean isSucceeded() { 
    boolean ret = delegate.isSucceeded();
    return ret;
  }

  /**
   * @return true if the this completion is completed and failed
   * @return 
   */
  public boolean isFailed() { 
    boolean ret = delegate.isFailed();
    return ret;
  }

  /**
   * Completion handler to receive a completion signal when this completions completes.
   * @param completionHandler the completion handler
   */
  public void handler(Handler<AsyncResult<T>> completionHandler) { 
    delegate.handler(completionHandler);
  }

  /**
   * Completion handler to receive a completion signal when this completions completes.
   * @return 
   */
  public Observable<T> handlerObservable() { 
    io.vertx.rx.java.ObservableFuture<T> completionHandler = io.vertx.rx.java.RxHelper.observableFuture();
    handler(completionHandler.toHandler());
    return completionHandler;
  }

  /**
   * Cause the current thread to wait until thi completion completes.<p/>
   *
   * If the current thread is interrupted, an exception will be thrown.
   */
  public void await() { 
    delegate.await();
  }

  /**
   * Cause the current thread to wait until this completion completes with a configurable timeout.<p/>
   *
   * If completion times out or the current thread is interrupted, an exception will be thrown.
   * @param timeoutMillis the timeout in milliseconds
   */
  public void await(long timeoutMillis) { 
    delegate.await(timeoutMillis);
  }

  /**
   * Cause the current thread to wait until this completion completes and succeeds.<p/>
   *
   * If the current thread is interrupted or the suite fails, an exception will be thrown.
   */
  public void awaitSuccess() { 
    delegate.awaitSuccess();
  }

  /**
   * Cause the current thread to wait until this completion completes and succeeds with a configurable timeout.<p/>
   *
   * If completion times out or the current thread is interrupted or the suite fails, an exception will be thrown.
   * @param timeoutMillis the timeout in milliseconds
   */
  public void awaitSuccess(long timeoutMillis) { 
    delegate.awaitSuccess(timeoutMillis);
  }


  public static <T> Completion newInstance(io.vertx.ext.unit.Completion arg) {
    return arg != null ? new Completion<T> (arg) : null;
  }
}
