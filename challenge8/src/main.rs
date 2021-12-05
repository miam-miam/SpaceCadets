#![no_std]
#![no_main]

extern crate alloc;

use alloc::vec;
use alloc::vec::Vec;
use core::panic::PanicInfo;

use bootloader::{entry_point, BootInfo};

use bb_macro::bb;
use challenge8::println;
use challenge8::task::{executor::Executor, keyboard, Task};

entry_point!(kernel_main);

fn kernel_main(boot_info: &'static BootInfo) -> ! {
    use challenge8::allocator;
    use challenge8::memory::{self, BootInfoFrameAllocator};
    use x86_64::VirtAddr;

    challenge8::init();

    let phys_mem_offset = VirtAddr::new(boot_info.physical_memory_offset);
    let mut mapper = unsafe { memory::init(phys_mem_offset) };
    let mut frame_allocator = unsafe { BootInfoFrameAllocator::init(&boot_info.memory_map) };

    allocator::init_heap(&mut mapper, &mut frame_allocator).expect("heap initialization failed");

    let mut executor = Executor::default();
    executor.spawn(Task::new_task_switcher(example_task()));
    executor.spawn(Task::new(keyboard::print_keypresses()));
    executor.run();
}

/// This function is called on panic.
#[panic_handler]
fn panic(info: &PanicInfo) -> ! {
    println!("{}", info);
    challenge8::hlt_loop();
}

async fn async_number() -> u32 {
    bb! {
        clear x;
        incr x;
        incr x;
        clear 'x;
        while x not 0 do;
            incr 'x;
            incr 'x;
            incr 'x;
            decr x;
        end;
    }
    x
}

async fn example_task() -> Vec<Task> {
    let number = async_number().await;
    println!("async number: {}", number);
    vec![Task::new(example_task2()), Task::new(example_task2())]
}

async fn example_task2() {
    let number = async_number().await;
    println!("async number: {}", number + 1);
}
