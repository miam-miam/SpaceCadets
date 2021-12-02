#![no_std]
#![no_main]
#![feature(core_intrinsics)]
#![feature(abi_x86_interrupt)]

mod gdt;
mod interrupts;
mod logger;

use bootloader::{entry_point, BootInfo};
use core::panic::PanicInfo;

entry_point!(kernel_main);

fn kernel_main(boot_info: &'static mut BootInfo) -> ! {
    if let Some(framebuffer) = boot_info.framebuffer.as_mut() {
        logger::LOGGER.lock().instantiate(framebuffer);
    }
    gdt::init();
    interrupts::init_idt();
    println!("test");
    println!("hoi");
    loop {}
}

#[panic_handler]
fn panic(info: &PanicInfo) -> ! {
    println!("{}", info);
    loop {}
}
