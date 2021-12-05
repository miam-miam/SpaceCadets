#![no_std]
#![feature(abi_x86_interrupt)]
#![feature(alloc_error_handler)]
#![feature(const_mut_refs)]

/// A lot of the stuff to get the kernel working is heavily based off Philipp Oppermann's blog (https://os.phil-opp.com/)
extern crate alloc;

pub mod allocator;
pub mod fixed_size_block;
pub mod gdt;
pub mod interrupts;
pub mod memory;
pub mod task;
pub mod vga_buffer;

pub fn init() {
    gdt::init();
    interrupts::init_idt();
    unsafe { interrupts::PICS.lock().initialize() };
    x86_64::instructions::interrupts::enable();
}

pub fn hlt_loop() -> ! {
    loop {
        x86_64::instructions::hlt();
    }
}

#[alloc_error_handler]
fn alloc_error_handler(layout: alloc::alloc::Layout) -> ! {
    panic!("allocation error: {:?}", layout)
}
