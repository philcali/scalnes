package calico.scalnes

import java.io.{FileInputStream, File}
import java.awt.image.BufferedImage
import java.awt.Color
import javax.imageio.ImageIO

/**
 * NES object to load a NES rom
 <pre>
val lickle = NES load "lickle.nes"
 </pre>
 */
object NES {
  /** Provide a full path to a NES rom binary */
  def load(file: String) = {
    val fis = new FileInputStream(file)
    fis.skip(4L)
    val pgr = fis.read
    val chr = fis.read

    val trainer = fis.read & 0x4

    fis.skip(9L)

    if(trainer != 0) fis.skip(512L)

    // If the chrbanks is 0, then the sprites are
    // embedded in the pgr blocks
    val multiplier = if(chr == 0) pgr else { 
      // skip the pgr garbage
      fis.skip(pgr * 16384) 
      chr
    }

    val chrbanks = new Array[Byte](8192 * multiplier)
    val read = fis.read(chrbanks)
    fis.close

    new NES(chr, chrbanks)
  }
}

/** An NES class to parse out NES tiles 
<pre>
lickle process >> "allsprites.gif"
val banks_3 = lickle process 3
val banks_0_10 = lickle process(0 to 10)
</pre>
*/
class NES(val chr: Int, val data: Array[Byte]) {
  def process(id: Int) = {
    val location = id * 8192
    new BankSegment(data slice(location, (location + 1) + 8192), false)
  }

  def process(r: Range) = {
    val start = r.head * 8192
    val size = (start + r.size) * 8192
    new BankSegment(data slice(start, size), false)
  }

  def process = {
    new BankSegment(data, false)
  }

  def >> (filename: String) = process >> (filename)
}

/** A Character Bank Segment that contains sprite info. This class actually saves the sprites to a file
<pre>
banks_3 tiles(375 to 435) horizontally >> "lickle_fonts.gif"
</pre>
*/
class BankSegment(val data: Array[Byte], val horizontal: Boolean) {
  val tileNo = data.length / 16
  val (x, y) = {
      if (tileNo > 16) { 
        if (horizontal) (tileNo, 1) else (16, (tileNo / 16)) 
      } else (tileNo,  1)
  }

  def horizontally = new BankSegment(data, true)

  def tiles(ts: Range) = {
    new BankSegment(data slice(ts.head * 16, (ts.last + 1) * 16), horizontal)
  }

  def >> (filename: String) = process(filename)

  private def process(filename: String) {
    val bi = new BufferedImage(x * 8, y * 8, BufferedImage.TYPE_INT_RGB)
    // process each tile at a time for each row
    for(row <- 0 until y; column <- 0 until x) {
      val increment = (row * 256) + (column * 16)
      val rtn = new scala.collection.mutable.ArrayBuffer[Int]()
      for(xi <- increment until (8 + increment); yi <- 0 until 8) {
        val v = (((data(xi) >> (7 - yi)) & 1) +
                ((data(xi + 8) >> (7 - yi)) & 1) +
                ((data(xi + 8) >> (7 - yi)) & 1)) * 85
        val color = new Color(v, v, v)
        rtn += color.getRGB
      }
      bi.setRGB((8 * column),(8 * row), 8, 8, rtn.toArray,0,8)
    }
    ImageIO.write(bi, "gif", new File(filename))
  }
}

// I want it to work this way:
// nes process >> "out.gif"
// nes process 0 >> "out.gif"
// nes process 0 until 3 >> "out.gif"
// nes process 0 tiles 0 until 32 >> "out.gif"
// nes process 0 until 8 tiles 16 until 74 >> "out.gif"

object Scalnes {
  def main(args: Array[String]) {
    if (args.size == 0) {
      println("provide me a .nes file")
      exit(0)
    }
    val nes = NES load args(0)
    nes.process(0) >> ("all.gif")
  }
}
