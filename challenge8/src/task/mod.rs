use alloc::boxed::Box;
use alloc::vec;
use alloc::vec::Vec;
use core::{
    future::Future,
    pin::Pin,
    sync::atomic::{AtomicU64, Ordering},
    task::{Context, Poll},
};

use futures_util::FutureExt;

pub mod executor;
pub mod keyboard;

/// A task contains the info to run an async future,
/// each taskId is unique so that the same task isn't put twice in the executor.
pub struct Task {
    id: TaskId,
    future: Pin<Box<dyn Future<Output = Vec<Task>>>>,
}

impl Task {
    /// A task switcher which can return tasks to switch to
    pub fn new_task_switcher(future: impl Future<Output = Vec<Task>> + 'static) -> Task {
        Task {
            id: TaskId::new(),
            future: Box::pin(future),
        }
    }

    /// A normal task that doesn't return any tasks.
    /// An empty vec does not allocate any mem on the heap so this is still really fast.
    pub fn new(future: impl Future<Output = ()> + 'static) -> Task {
        Task {
            id: TaskId::new(),
            future: Box::pin(future.map(|_| vec![])),
        }
    }

    fn poll(&mut self, context: &mut Context) -> Poll<Vec<Task>> {
        self.future.as_mut().poll(context)
    }
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, PartialOrd, Ord)]
struct TaskId(u64);

impl TaskId {
    fn new() -> Self {
        static NEXT_ID: AtomicU64 = AtomicU64::new(0);
        TaskId(NEXT_ID.fetch_add(1, Ordering::Relaxed))
    }
}
