Logo Generator
==============

Generation of logo in all known formats

Usage
-----
Requirements:
*	ImageMagick

	Download page:
http://www.imagemagick.org/script/binary-releases.php.

	Environment variables: `IMCONV` should point to ImageMagick's
`convert` executable (see
http://www.imagemagick.org/Usage/windows/#convert_issue for
detailed description of issue).

Additional notes and references for developers
----------------------------------------------
*	Some image formats, including PNG, store embedded information about
dot density (aka DPI). Wrong DPI sometimes causes problem. Examples:
	*
http://www.hanselman.com/blog/BeAwareOfDPIWithImagePNGsInWPFImagesScaleWeirdOrAreBlurry.aspx
	*	http://habrahabr.ru/post/216833/#part3
	
	Default didplay resolution is 96 dpi, and that value should be set
in metadata of all images.
*	Inkscape exports images as 90 dpi, so it can't be used for building.
*	Some operating systems provide High DPI mode.
*	Required and recommended sizes of icons with respect to High DPI
mode:
	
*	Additional reading:
	*
https://blog.qt.digia.com/blog/2013/04/25/retina-display-support-for-mac-os-ios-and-x11/

------------------------------------------------------------------------
Copyright © 2014, 2018  Basil Peace

This is part of Logo Generator.

Copying and distribution of this file, with or without modification,
are permitted in any medium without royalty provided the copyright
notice and this notice are preserved.  This file is offered as-is,
without any warranty.
