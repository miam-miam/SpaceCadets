use core::{fmt, ptr};

use bootloader::boot_info::{FrameBuffer, FrameBufferInfo, PixelFormat};
use font8x8::UnicodeFonts;
use lazy_static::lazy_static;
use spin::Mutex;

lazy_static! {
    pub static ref LOGGER: Mutex<Logger> = Mutex::new(Logger {
        framebuffer: None,
        info: None,
        x_pos: 0,
        y_pos: 0
    });
}

/// Additional vertical space between lines
const LINE_SPACING: usize = 0;

pub struct Logger {
    framebuffer: Option<&'static mut [u8]>,
    info: Option<FrameBufferInfo>,
    x_pos: usize,
    y_pos: usize,
}

impl Logger {
    pub fn instantiate(&mut self, buffer: &'static mut FrameBuffer) {
        self.info = Some(buffer.info());
        self.framebuffer = Some(buffer.buffer_mut());
        self.clear();
    }

    fn newline(&mut self) {
        self.y_pos += 8 + LINE_SPACING;
        self.carriage_return()
    }

    pub fn add_vspace(&mut self, space: usize) {
        self.y_pos += space;
    }

    fn carriage_return(&mut self) {
        self.x_pos = 0;
    }

    /// Erases all text on the screen.
    pub fn clear(&mut self) {
        self.x_pos = 0;
        self.y_pos = 0;
        self.framebuffer.as_mut().unwrap().fill(0);
    }

    fn width(&self) -> usize {
        self.info.unwrap().horizontal_resolution
    }

    fn height(&self) -> usize {
        self.info.unwrap().vertical_resolution
    }

    fn write_char(&mut self, c: char) {
        match c {
            '\n' => self.newline(),
            '\r' => self.carriage_return(),
            c => {
                if self.x_pos >= self.width() {
                    self.newline();
                }
                if self.y_pos >= (self.height() - 8) {
                    self.clear();
                }
                let rendered = font8x8::BASIC_FONTS
                    .get(c)
                    .expect("character not found in basic font");
                self.write_rendered_char(rendered);
            }
        }
    }

    fn write_rendered_char(&mut self, rendered_char: [u8; 8]) {
        for (y, byte) in rendered_char.iter().enumerate() {
            for (x, bit) in (0..8).enumerate() {
                let alpha = if *byte & (1 << bit) == 0 { 0 } else { 255 };
                self.write_pixel(self.x_pos + x, self.y_pos + y, alpha);
            }
        }
        self.x_pos += 8;
    }

    fn write_pixel(&mut self, x: usize, y: usize, intensity: u8) {
        let pixel_offset = y * self.info.unwrap().stride + x;
        let color = match self.info.unwrap().pixel_format {
            PixelFormat::RGB => [intensity, intensity, intensity / 2, 0],
            PixelFormat::BGR => [intensity / 2, intensity, intensity, 0],
            PixelFormat::U8 => [if intensity > 200 { 0xf } else { 0 }, 0, 0, 0],
            _ => unreachable!(),
        };
        let bytes_per_pixel = self.info.unwrap().bytes_per_pixel;
        let byte_offset = pixel_offset * bytes_per_pixel;
        self.framebuffer.as_mut().unwrap()[byte_offset..(byte_offset + bytes_per_pixel)]
            .copy_from_slice(&color[..bytes_per_pixel]);
        let _ = unsafe { ptr::read_volatile(&self.framebuffer.as_mut().unwrap()[byte_offset]) };
    }
}

impl fmt::Write for Logger {
    fn write_str(&mut self, s: &str) -> fmt::Result {
        for c in s.chars() {
            self.write_char(c);
        }
        Ok(())
    }
}

#[macro_export]
macro_rules! print {
    ($($arg:tt)*) => ($crate::logger::_print(format_args!($($arg)*)));
}

#[macro_export]
macro_rules! println {
    () => ($crate::print!("\n"));
    ($fmt:expr) => ($crate::print!(concat!($fmt, "\n")));
    ($fmt:expr, $($arg:tt)*) => ($crate::print!(concat!($fmt, "\n"), $($arg)*));
}

#[doc(hidden)]
pub fn _print(args: fmt::Arguments) {
    use core::fmt::Write;
    LOGGER.lock().write_fmt(args).unwrap();
}
