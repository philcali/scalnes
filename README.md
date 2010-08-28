Scalnes
=======

Requirements
------

 * scala 2.8.0
 * sbt 0.7.4

What is Scalnes?
------

It is **not** an emulator written in Scala! Those of you looking for that should look somewhere else. 

Scalnes is a commandline tool that rips NES sprites from NES roms.

How do I use it? 
------

    git clone git://github.com/philcali/scalnes.git
    cd scalnes
    sbt
    console

Once you are inside the scala console, testing it out is pretty easy.

    import calico.scalnes.NES

    // Loading a rom is fairly easy... Just provide the full path
    val nes_rom = NES load "legally_owned_rom.nes"
    nes_rom.process >> "all_sprites.gif"

    // Process chr bank 3 of either 8 or 16
    nes_rom.process(3) >> "chr_bank3_sprites.gif"

    // Process specific tiles from chr bank data
    nes_rom.process(3) tiles(256 to 326) >> "main_character_sprites.gif"

    // You can even process horizontally for special cases
    nes_rom.process(15) tiles(446 to 526) horizontally >> "game_fonts.gif"
