/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeMap;

import java.net.InetSocketAddress;
import java.net.SocketAddress;


/**
 * A nexus to a network socket or a component which is capable of I/O
 * operations such as read, write, connect, and bind.
 * <p>
 * A channel provides a user:
 * <ul>
 * <li>the current state of the channel (e.g. is it open? is it connected?),</li>
 * <li>the {@linkplain ChannelConfig configuration parameters} of the channel (e.g. receive buffer size),</li>
 * <li>the I/O operations that the channel supports (e.g. read, write, connect, and bind), and</li>
 * <li>the {@link ChannelPipeline} which handles all I/O events and requests
 *     associated with the channel.</li>
 * </ul>
 *
 * <h3>All I/O operations are asynchronous.</h3>
 * <p>
 * All I/O operations in Netty are asynchronous.  It means any I/O calls will
 * return immediately with no guarantee that the requested I/O operation has
 * been completed at the end of the call.  Instead, you will be returned with
 * a {@link ChannelFuture} instance which will notify you when the requested I/O
 * operation has succeeded, failed, or canceled.
 *
 * <h3>Channels are hierarchical</h3>
 * <p>
 * A {@link Channel} can have a {@linkplain #parent() parent} depending on
 * how it was created.  For instance, a {@link SocketChannel}, that was accepted
 * by {@link ServerSocketChannel}, will return the {@link ServerSocketChannel}
 * as its parent on {@link #parent()}.
 * <p>
 * The semantics of the hierarchical structure depends on the transport
 * implementation where the {@link Channel} belongs to.  For example, you could
 * write a new {@link Channel} implementation that creates the sub-channels that
 * share one socket connection, as <a href="http://beepcore.org/">BEEP</a> and
 * <a href="http://en.wikipedia.org/wiki/Secure_Shell">SSH</a> do.
 *
 * <h3>Downcast to access transport-specific operations</h3>
 * <p>
 * Some transports exposes additional operations that is specific to the
 * transport.  Down-cast the {@link Channel} to sub-type to invoke such
 * operations.  For example, with the old I/O datagram transport, multicast
 * join / leave operations are provided by {@link DatagramChannel}.
 *
 * <h3>Release resources</h3>
 * <p>
 * It is important to call {@link #close()} or {@link #close(ChannelPromise)} to release all
 * resources once you are done with the {@link Channel}. This ensures all resources are
 * released in a proper way, i.e. filehandles.
 */
/**
 * Channel接口，封装了与网络套接字能够进行I/O操作(读取、写入、连接、绑定)的组件
 * 为用户提供：
 * 1.通道当前状态，是否打开、是否已连接
 * 2.ChannelConfig，通道的配置参数
 * 3.通道支持的I/O操作，读取、写入、连接、绑定
 * 4.ChannelPipeline，处理所有与通道关联的I/O事件和请求
 *
 * 所有I/O操作都是异步的
 * Netty中所有I/O操作都是异步的，意味着任何I/O调用都将立即返回，而不能保证所请求的I/O操作在调用结束时已完成
 * 相反，将返回一个{@link ChannelFuture}实例，该实例将在请求的I/O操作成功、失败或取消时通知您
 *
 * 通道是可继承的、分层的
 * 一个Channel可以具有一个{@link #parent()}的父级Channel，这取决于它的创建方式
 * 例如：{@link ServerSocketChannel}接受的{@link SocketChannel}将返回{@link ServerSocketChannel}作为其{@link #parent()}的返回值
 * 层次结构的语义取决于{@link Channel}所属的传输实现，以创建共享一个套接字连接的子通道
 *
 * 向下访问特定于传输的操作
 * 某些传输公开了特定于传输的其他操作，将{@link Channel}向下转换为子类型以调用此类操作。
 * 例如：对于旧的I/O数据报传输，{@link DatagramChannel}提供了多播加入/离开操作
 *
 * 释放资源
 * 完成操作后，调用{@link #close()}或者{@link #close(ChannelPromise)}释放所有资源很重要
 * 这样可以确保以适当的方式(即文件句柄)释放所有资源
 */
public interface Channel extends AttributeMap, ChannelOutboundInvoker, Comparable<Channel> {

    /**
     * Returns the globally unique identifier of this {@link Channel}.
     */
    /* 返回全局唯一标识符 */
    ChannelId id();

    /**
     * Return the {@link EventLoop} this {@link Channel} was registered to.
     */
    /* 返回此Channel注册的事件循环 */
    EventLoop eventLoop();

    /**
     * Returns the parent of this channel.
     *
     * @return the parent channel.
     *         {@code null} if this channel does not have a parent channel.
     */
    /* 返回父级Channel */
    Channel parent();

    /**
     * Returns the configuration of this channel.
     */
    /* 返回配置 */
    ChannelConfig config();

    /**
     * Returns {@code true} if the {@link Channel} is open and may get active later
     */
    /* 是否打开 */
    boolean isOpen();

    /**
     * Returns {@code true} if the {@link Channel} is registered with an {@link EventLoop}.
     */
    /* 是否已经向事件循环注册 */
    boolean isRegistered();

    /**
     * Return {@code true} if the {@link Channel} is active and so connected.
     */
    /* 是否激活 */
    boolean isActive();

    /**
     * Return the {@link ChannelMetadata} of the {@link Channel} which describe the nature of the {@link Channel}.
     */
    /* 返回元数据 */
    ChannelMetadata metadata();

    /**
     * Returns the local address where this channel is bound to.  The returned
     * {@link SocketAddress} is supposed to be down-cast into more concrete
     * type such as {@link InetSocketAddress} to retrieve the detailed
     * information.
     *
     * @return the local address of this channel.
     *         {@code null} if this channel is not bound.
     */
    /* 返回绑定的本地地址 */
    SocketAddress localAddress();

    /**
     * Returns the remote address where this channel is connected to.  The
     * returned {@link SocketAddress} is supposed to be down-cast into more
     * concrete type such as {@link InetSocketAddress} to retrieve the detailed
     * information.
     *
     * @return the remote address of this channel.
     *         {@code null} if this channel is not connected.
     *         If this channel is not connected but it can receive messages
     *         from arbitrary remote addresses (e.g. {@link DatagramChannel},
     *         use {@link DatagramPacket#recipient()} to determine
     *         the origination of the received message as this method will
     *         return {@code null}.
     */
    /* 返回连接到的远程地址 */
    SocketAddress remoteAddress();

    /**
     * Returns the {@link ChannelFuture} which will be notified when this
     * channel is closed.  This method always returns the same future instance.
     */
    /** 返回{@link ChannelFuture}，当关闭此通道时，将收到通知 */
    ChannelFuture closeFuture();

    /**
     * Returns {@code true} if and only if the I/O thread will perform the
     * requested write operation immediately.  Any write requests made when
     * this method returns {@code false} are queued until the I/O thread is
     * ready to process the queued write requests.
     */
    /**
     * 当且仅当I/O线程将立即执行请求的写操作时返回true
     * 当此方法返回false时，发出的所有写请求都将排队，直到I/O线程准备好处理排队的写请求为止
     */
    boolean isWritable();

    /**
     * Get how many bytes can be written until {@link #isWritable()} returns {@code false}.
     * This quantity will always be non-negative. If {@link #isWritable()} is {@code false} then 0.
     */
    /**
     * 获取直到{@link #isWritable()}返回false为止可以写入的字节数，此数量将始终为非负数
     * 如果{@link #isWritable()}为false，则为0
     */
    long bytesBeforeUnwritable();

    /**
     * Get how many bytes must be drained from underlying buffers until {@link #isWritable()} returns {@code true}.
     * This quantity will always be non-negative. If {@link #isWritable()} is {@code true} then 0.
     */
    /**
     * 获取直到{@link #isWritable()}返回true为止，必须从底层缓冲区中耗尽多少字节
     * 如果{@link #isWritable()}为true，则为0
     */
    long bytesBeforeWritable();

    /**
     * Returns an <em>internal-use-only</em> object that provides unsafe operations.
     */
    /* 内存不安全操作对象 */
    Unsafe unsafe();

    /**
     * Return the assigned {@link ChannelPipeline}.
     */
    /* 分配的管道 */
    ChannelPipeline pipeline();

    /**
     * Return the assigned {@link ByteBufAllocator} which will be used to allocate {@link ByteBuf}s.
     */
    /* 字节缓冲分配器，用于分配字节缓冲 */
    ByteBufAllocator alloc();

    @Override
    Channel read();

    @Override
    Channel flush();

    /**
     * <em>Unsafe</em> operations that should <em>never</em> be called from user-code. These methods
     * are only provided to implement the actual transport, and must be invoked from an I/O thread except for the
     * following methods:
     * <ul>
     *   <li>{@link #localAddress()}</li>
     *   <li>{@link #remoteAddress()}</li>
     *   <li>{@link #closeForcibly()}</li>
     *   <li>{@link #register(EventLoop, ChannelPromise)}</li>
     *   <li>{@link #deregister(ChannelPromise)}</li>
     *   <li>{@link #voidPromise()}</li>
     * </ul>
     */
    /**
     * 用于内部调用的不安全操作，这些方法仅用于实现实际的传输，并且必须从I/O线程调用，但以下方法除外：
     * {@link #localAddress()}
     * {@link #remoteAddress()}
     * {@link #closeForcibly()}
     * {@link #register(EventLoop, ChannelPromise)}
     * {@link #deregister(ChannelPromise)}
     * {@link #voidPromise()}
     */
    interface Unsafe {

        /**
         * Return the assigned {@link RecvByteBufAllocator.Handle} which will be used to allocate {@link ByteBuf}'s when
         * receiving data.
         */
        /**
         * 返回分配的{@link RecvByteBufAllocator.Handle}，当接收数据时将用于分配{@link ByteBuf}
         * @return
         */
        RecvByteBufAllocator.Handle recvBufAllocHandle();

        /**
         * Return the {@link SocketAddress} to which is bound local or
         * {@code null} if none.
         */
        /** 返回绑定的本地地址 */
        SocketAddress localAddress();

        /**
         * Return the {@link SocketAddress} to which is bound remote or
         * {@code null} if none is bound yet.
         */
        /** 返回绑定的远程地址 */
        SocketAddress remoteAddress();

        /**
         * Register the {@link Channel} of the {@link ChannelPromise} and notify
         * the {@link ChannelFuture} once the registration was complete.
         */
        /**
         * 注册{@link ChannelPromise}的{@link Channel}并且在注册完成后通知{@link ChannelFuture}
         */
        void register(EventLoop eventLoop, ChannelPromise promise);

        /**
         * Bind the {@link SocketAddress} to the {@link Channel} of the {@link ChannelPromise} and notify
         * it once its done.
         */
        /**
         * 将{@link SocketAddress}绑定到{@link ChannelPromise}的{@link Channel}
         * 并在完成后通知
         */
        void bind(SocketAddress localAddress, ChannelPromise promise);

        /**
         * Connect the {@link Channel} of the given {@link ChannelFuture} with the given remote {@link SocketAddress}.
         * If a specific local {@link SocketAddress} should be used it need to be given as argument. Otherwise just
         * pass {@code null} to it.
         *
         * The {@link ChannelPromise} will get notified once the connect operation was complete.
         */
        /**
         * 将给定的{@link ChannelFuture}的{@link Channel}连接到远程地址
         * 如果需要指定的本地地址，则应传递相应参数
         * {@link ChannelPromise}将在连接完成后得到通知
         */
        void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise);

        /**
         * Disconnect the {@link Channel} of the {@link ChannelFuture} and notify the {@link ChannelPromise} once the
         * operation was complete.
         */
        /**
         * 断开连接，并且通知{@link ChannelPromise}
         */
        void disconnect(ChannelPromise promise);

        /**
         * Close the {@link Channel} of the {@link ChannelPromise} and notify the {@link ChannelPromise} once the
         * operation was complete.
         */
        /** 关闭并通知 */
        void close(ChannelPromise promise);

        /**
         * Closes the {@link Channel} immediately without firing any events.  Probably only useful
         * when registration attempt failed.
         */
        /** 立即关闭而不触发任何事件 */
        void closeForcibly();

        /**
         * Deregister the {@link Channel} of the {@link ChannelPromise} from {@link EventLoop} and notify the
         * {@link ChannelPromise} once the operation was complete.
         */
        /** 从{@link EventLoop}中注销{@link ChannelPromise}的{@link Channel} */
        void deregister(ChannelPromise promise);

        /**
         * Schedules a read operation that fills the inbound buffer of the first {@link ChannelInboundHandler} in the
         * {@link ChannelPipeline}.  If there's already a pending read operation, this method does nothing.
         */
        /**
         * 调度读取操作，该操作将填充{@link ChannelPipeline}中第一个{@link ChannelInboundHandler}的入站缓冲区
         * 如果已经右待处理的读取操作，则此方法不执行任何操作
         */
        void beginRead();

        /**
         * Schedules a write operation.
         */
        /** 调度一个写操作 */
        void write(Object msg, ChannelPromise promise);

        /**
         * Flush out all write operations scheduled via {@link #write(Object, ChannelPromise)}.
         */
        /** 清除所有通过{@link #write(Object, ChannelPromise)}调度的写操作 */
        void flush();

        /**
         * Return a special ChannelPromise which can be reused and passed to the operations in {@link Unsafe}.
         * It will never be notified of a success or error and so is only a placeholder for operations
         * that take a {@link ChannelPromise} as argument but for which you not want to get notified.
         */
        /** 返回一个特殊的{@link ChannelPromise}，可以重复使用并将其传递给{@link Unsafe}中的操作
         *  永远不会通知它成功或错误，所以它只是一个占位符
         */
        ChannelPromise voidPromise();

        /**
         * Returns the {@link ChannelOutboundBuffer} of the {@link Channel} where the pending write requests are stored.
         */
        /** 返回{@link Channel}的{@link ChannelOutboundBuffer}，存储了pending状态的写请求 */
        ChannelOutboundBuffer outboundBuffer();
    }
}
