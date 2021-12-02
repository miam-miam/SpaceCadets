#![no_std]
#![no_main]
#![feature(core_intrinsics)]

mod logger;

use crate::logger::Logger;
use bootloader::{entry_point, BootInfo};
use core::fmt::Write;
use core::panic::PanicInfo;

entry_point!(kernel_main);

fn kernel_main(boot_info: &'static mut BootInfo) -> ! {
    if let Some(framebuffer) = boot_info.framebuffer.as_mut() {
        let info = framebuffer.info();
        let mut logger = Logger::new(framebuffer.buffer_mut(), info);
        write!(logger, "Test").unwrap();
        writeln!(logger, "a").unwrap();
    }

    loop {}
}

#[panic_handler]
fn panic(_info: &PanicInfo) -> ! {
    loop {}
}
