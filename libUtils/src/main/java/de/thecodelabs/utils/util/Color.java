package de.thecodelabs.utils.util;

import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Color
{
	private static final int MAX = 255;
	private static final int HEX_BASE = 16;

	private int red;
	private int green;
	private int blue;
	private int alpha;

	public Color(float red, float green, float blue)
	{
		this(red, green, blue, 1.0f);
	}

	public Color(float red, float green, float blue, float alpha)
	{
		this.red = (int) (red * 255);
		this.green = (int) (green * 255);
		this.blue = (int) (blue * 255);
		this.alpha = (int) (alpha * 255);
	}

	public Color(int red, int green, int blue)
	{
		this(red, green, blue, MAX);
	}

	public Color(int red, int green, int blue, int alpha)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public Color(String hex)
	{
		hex = hex.replace("#", "");
		if(hex.length() == 3)
		{
			parseHexShort(hex);
		}
		else if(hex.length() == 6)
		{
			parseHexLong(hex);
		}
		else if(hex.length() == 8)
		{
			parseHexLongAlpha(hex);
		}
		else
		{
			throw new IllegalArgumentException(hex + " is not a valid hex code");
		}
	}

	private void parseHexLong(String hex)
	{
		long value = Long.parseLong(hex, HEX_BASE);
		this.red = (int) ((value & 0xFF0000) >> 16);
		this.green = (int) ((value & 0xFF00) >> 8);
		this.blue = (int) (value & 0xFF);
		this.alpha = MAX;
	}

	private void parseHexLongAlpha(String hex)
	{
		long value = Long.parseLong(hex, HEX_BASE);
		this.red = (int) ((value & 0xFF000000) >> 24);
		this.green = (int) ((value & 0xFF0000) >> 16);
		this.blue = (int) ((value & 0xFF00) >> 8);
		this.alpha = (int) (value & 0xFF);
	}

	private void parseHexShort(String hex)
	{
		int value = Integer.parseInt(hex, HEX_BASE);
		this.red = ((value & 0xF00) >> 8) * 0x11;
		this.green = ((value & 0xF0) >> 4) * 0x11;
		this.blue = (value & 0xF) * 0x11;
		this.alpha = MAX;
	}

	public int getRed()
	{
		return red;
	}

	public int getGreen()
	{
		return green;
	}

	public int getBlue()
	{
		return blue;
	}

	public int getAlpha()
	{
		return alpha;
	}

	public void setRed(int red)
	{
		this.red = red;
	}

	public void setGreen(int green)
	{
		this.green = green;
	}

	public void setBlue(int blue)
	{
		this.blue = blue;
	}

	public void setAlpha(int alpha)
	{
		this.alpha = alpha;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Color color = (Color) o;
		return red == color.red &&
				green == color.green &&
				blue == color.blue &&
				alpha == color.alpha;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(red, green, blue, alpha);
	}

	@Override
	public String toString()
	{
		return "Color{" +
				"red=" + red +
				", green=" + green +
				", blue=" + blue +
				", alpha=" + alpha +
				'}';
	}

	public String toRGBHexWithoutOpacity()
	{
		return ColorUtilsNonJavaFX.toRGBHexWithoutOpacity(this);
	}

	public String toRGBHex()
	{
		return ColorUtilsNonJavaFX.toRGBHex(this);
	}

	public static final Color TRANSPARENT = new Color(0f, 0f, 0f, 0f);
	public static final Color ALICEBLUE = new Color(0.9411765f, 0.972549f, 1.0f);
	public static final Color ANTIQUEWHITE = new Color(0.98039216f, 0.92156863f, 0.84313726f);
	public static final Color AQUA = new Color(0.0f, 1.0f, 1.0f);
	public static final Color AQUAMARINE = new Color(0.49803922f, 1.0f, 0.83137256f);
	public static final Color AZURE = new Color(0.9411765f, 1.0f, 1.0f);
	public static final Color BEIGE = new Color(0.9607843f, 0.9607843f, 0.8627451f);
	public static final Color BISQUE = new Color(1.0f, 0.89411765f, 0.76862746f);
	public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f);
	public static final Color BLANCHEDALMOND = new Color(1.0f, 0.92156863f, 0.8039216f);
	public static final Color BLUE = new Color(0.0f, 0.0f, 1.0f);
	public static final Color BLUEVIOLET = new Color(0.5411765f, 0.16862746f, 0.8862745f);
	public static final Color BROWN = new Color(0.64705884f, 0.16470589f, 0.16470589f);
	public static final Color BURLYWOOD = new Color(0.87058824f, 0.72156864f, 0.5294118f);
	public static final Color CADETBLUE = new Color(0.37254903f, 0.61960787f, 0.627451f);
	public static final Color CHARTREUSE = new Color(0.49803922f, 1.0f, 0.0f);
	public static final Color CHOCOLATE = new Color(0.8235294f, 0.4117647f, 0.11764706f);
	public static final Color CORAL = new Color(1.0f, 0.49803922f, 0.3137255f);
	public static final Color CORNFLOWERBLUE = new Color(0.39215687f, 0.58431375f, 0.92941177f);
	public static final Color CORNSILK = new Color(1.0f, 0.972549f, 0.8627451f);
	public static final Color CRIMSON = new Color(0.8627451f, 0.078431375f, 0.23529412f);
	public static final Color CYAN = new Color(0.0f, 1.0f, 1.0f);
	public static final Color DARKBLUE = new Color(0.0f, 0.0f, 0.54509807f);
	public static final Color DARKCYAN = new Color(0.0f, 0.54509807f, 0.54509807f);
	public static final Color DARKGOLDENROD = new Color(0.72156864f, 0.5254902f, 0.043137256f);
	public static final Color DARKGRAY = new Color(0.6627451f, 0.6627451f, 0.6627451f);
	public static final Color DARKGREEN = new Color(0.0f, 0.39215687f, 0.0f);
	public static final Color DARKGREY = DARKGRAY;
	public static final Color DARKKHAKI = new Color(0.7411765f, 0.7176471f, 0.41960785f);
	public static final Color DARKMAGENTA = new Color(0.54509807f, 0.0f, 0.54509807f);
	public static final Color DARKOLIVEGREEN = new Color(0.33333334f, 0.41960785f, 0.18431373f);
	public static final Color DARKORANGE = new Color(1.0f, 0.54901963f, 0.0f);
	public static final Color DARKORCHID = new Color(0.6f, 0.19607843f, 0.8f);
	public static final Color DARKRED = new Color(0.54509807f, 0.0f, 0.0f);
	public static final Color DARKSALMON = new Color(0.9137255f, 0.5882353f, 0.47843137f);
	public static final Color DARKSEAGREEN = new Color(0.56078434f, 0.7372549f, 0.56078434f);
	public static final Color DARKSLATEBLUE = new Color(0.28235295f, 0.23921569f, 0.54509807f);
	public static final Color DARKSLATEGRAY = new Color(0.18431373f, 0.30980393f, 0.30980393f);
	public static final Color DARKSLATEGREY = DARKSLATEGRAY;
	public static final Color DARKTURQUOISE = new Color(0.0f, 0.80784315f, 0.81960785f);
	public static final Color DARKVIOLET = new Color(0.5803922f, 0.0f, 0.827451f);
	public static final Color DEEPPINK = new Color(1.0f, 0.078431375f, 0.5764706f);
	public static final Color DEEPSKYBLUE = new Color(0.0f, 0.7490196f, 1.0f);
	public static final Color DIMGRAY = new Color(0.4117647f, 0.4117647f, 0.4117647f);
	public static final Color DIMGREY = DIMGRAY;
	public static final Color DODGERBLUE = new Color(0.11764706f, 0.5647059f, 1.0f);
	public static final Color FIREBRICK = new Color(0.69803923f, 0.13333334f, 0.13333334f);
	public static final Color FLORALWHITE = new Color(1.0f, 0.98039216f, 0.9411765f);
	public static final Color FORESTGREEN = new Color(0.13333334f, 0.54509807f, 0.13333334f);
	public static final Color FUCHSIA = new Color(1.0f, 0.0f, 1.0f);
	public static final Color GAINSBORO = new Color(0.8627451f, 0.8627451f, 0.8627451f);
	public static final Color GHOSTWHITE = new Color(0.972549f, 0.972549f, 1.0f);
	public static final Color GOLD = new Color(1.0f, 0.84313726f, 0.0f);
	public static final Color GOLDENROD = new Color(0.85490197f, 0.64705884f, 0.1254902f);
	public static final Color GRAY = new Color(0.5019608f, 0.5019608f, 0.5019608f);
	public static final Color GREEN = new Color(0.0f, 0.5019608f, 0.0f);
	public static final Color GREENYELLOW = new Color(0.6784314f, 1.0f, 0.18431373f);
	public static final Color GREY = GRAY;
	public static final Color HONEYDEW = new Color(0.9411765f, 1.0f, 0.9411765f);
	public static final Color HOTPINK = new Color(1.0f, 0.4117647f, 0.7058824f);
	public static final Color INDIANRED = new Color(0.8039216f, 0.36078432f, 0.36078432f);
	public static final Color INDIGO = new Color(0.29411766f, 0.0f, 0.50980395f);
	public static final Color IVORY = new Color(1.0f, 1.0f, 0.9411765f);
	public static final Color KHAKI = new Color(0.9411765f, 0.9019608f, 0.54901963f);
	public static final Color LAVENDER = new Color(0.9019608f, 0.9019608f, 0.98039216f);
	public static final Color LAVENDERBLUSH = new Color(1.0f, 0.9411765f, 0.9607843f);
	public static final Color LAWNGREEN = new Color(0.4862745f, 0.9882353f, 0.0f);
	public static final Color LEMONCHIFFON = new Color(1.0f, 0.98039216f, 0.8039216f);
	public static final Color LIGHTBLUE = new Color(0.6784314f, 0.84705883f, 0.9019608f);
	public static final Color LIGHTCORAL = new Color(0.9411765f, 0.5019608f, 0.5019608f);
	public static final Color LIGHTCYAN = new Color(0.8784314f, 1.0f, 1.0f);
	public static final Color LIGHTGOLDENRODYELLOW = new Color(0.98039216f, 0.98039216f, 0.8235294f);
	public static final Color LIGHTGRAY = new Color(0.827451f, 0.827451f, 0.827451f);
	public static final Color LIGHTGREEN = new Color(0.5647059f, 0.93333334f, 0.5647059f);
	public static final Color LIGHTGREY = LIGHTGRAY;
	public static final Color LIGHTPINK = new Color(1.0f, 0.7137255f, 0.75686276f);
	public static final Color LIGHTSALMON = new Color(1.0f, 0.627451f, 0.47843137f);
	public static final Color LIGHTSEAGREEN = new Color(0.1254902f, 0.69803923f, 0.6666667f);
	public static final Color LIGHTSKYBLUE = new Color(0.5294118f, 0.80784315f, 0.98039216f);
	public static final Color LIGHTSLATEGRAY = new Color(0.46666667f, 0.53333336f, 0.6f);
	public static final Color LIGHTSLATEGREY = LIGHTSLATEGRAY;
	public static final Color LIGHTSTEELBLUE = new Color(0.6901961f, 0.76862746f, 0.87058824f);
	public static final Color LIGHTYELLOW = new Color(1.0f, 1.0f, 0.8784314f);
	public static final Color LIME = new Color(0.0f, 1.0f, 0.0f);
	public static final Color LIMEGREEN = new Color(0.19607843f, 0.8039216f, 0.19607843f);
	public static final Color LINEN = new Color(0.98039216f, 0.9411765f, 0.9019608f);
	public static final Color MAGENTA = new Color(1.0f, 0.0f, 1.0f);
	public static final Color MAROON = new Color(0.5019608f, 0.0f, 0.0f);
	public static final Color MEDIUMAQUAMARINE = new Color(0.4f, 0.8039216f, 0.6666667f);
	public static final Color MEDIUMBLUE = new Color(0.0f, 0.0f, 0.8039216f);
	public static final Color MEDIUMORCHID = new Color(0.7294118f, 0.33333334f, 0.827451f);
	public static final Color MEDIUMPURPLE = new Color(0.5764706f, 0.4392157f, 0.85882354f);
	public static final Color MEDIUMSEAGREEN = new Color(0.23529412f, 0.7019608f, 0.44313726f);
	public static final Color MEDIUMSLATEBLUE = new Color(0.48235294f, 0.40784314f, 0.93333334f);
	public static final Color MEDIUMSPRINGGREEN = new Color(0.0f, 0.98039216f, 0.6039216f);
	public static final Color MEDIUMTURQUOISE = new Color(0.28235295f, 0.81960785f, 0.8f);
	public static final Color MEDIUMVIOLETRED = new Color(0.78039217f, 0.08235294f, 0.52156866f);
	public static final Color MIDNIGHTBLUE = new Color(0.09803922f, 0.09803922f, 0.4392157f);
	public static final Color MINTCREAM = new Color(0.9607843f, 1.0f, 0.98039216f);
	public static final Color MISTYROSE = new Color(1.0f, 0.89411765f, 0.88235295f);
	public static final Color MOCCASIN = new Color(1.0f, 0.89411765f, 0.70980394f);
	public static final Color NAVAJOWHITE = new Color(1.0f, 0.87058824f, 0.6784314f);
	public static final Color NAVY = new Color(0.0f, 0.0f, 0.5019608f);
	public static final Color OLDLACE = new Color(0.99215686f, 0.9607843f, 0.9019608f);
	public static final Color OLIVE = new Color(0.5019608f, 0.5019608f, 0.0f);
	public static final Color OLIVEDRAB = new Color(0.41960785f, 0.5568628f, 0.13725491f);
	public static final Color ORANGE = new Color(1.0f, 0.64705884f, 0.0f);
	public static final Color ORANGERED = new Color(1.0f, 0.27058825f, 0.0f);
	public static final Color ORCHID = new Color(0.85490197f, 0.4392157f, 0.8392157f);
	public static final Color PALEGOLDENROD = new Color(0.93333334f, 0.9098039f, 0.6666667f);
	public static final Color PALEGREEN = new Color(0.59607846f, 0.9843137f, 0.59607846f);
	public static final Color PALETURQUOISE = new Color(0.6862745f, 0.93333334f, 0.93333334f);
	public static final Color PALEVIOLETRED = new Color(0.85882354f, 0.4392157f, 0.5764706f);
	public static final Color PAPAYAWHIP = new Color(1.0f, 0.9372549f, 0.8352941f);
	public static final Color PEACHPUFF = new Color(1.0f, 0.85490197f, 0.7254902f);
	public static final Color PERU = new Color(0.8039216f, 0.52156866f, 0.24705882f);
	public static final Color PINK = new Color(1.0f, 0.7529412f, 0.79607844f);
	public static final Color PLUM = new Color(0.8666667f, 0.627451f, 0.8666667f);
	public static final Color POWDERBLUE = new Color(0.6901961f, 0.8784314f, 0.9019608f);
	public static final Color PURPLE = new Color(0.5019608f, 0.0f, 0.5019608f);
	public static final Color RED = new Color(1.0f, 0.0f, 0.0f);
	public static final Color ROSYBROWN = new Color(0.7372549f, 0.56078434f, 0.56078434f);
	public static final Color ROYALBLUE = new Color(0.25490198f, 0.4117647f, 0.88235295f);
	public static final Color SADDLEBROWN = new Color(0.54509807f, 0.27058825f, 0.07450981f);
	public static final Color SALMON = new Color(0.98039216f, 0.5019608f, 0.44705883f);
	public static final Color SANDYBROWN = new Color(0.95686275f, 0.6431373f, 0.3764706f);
	public static final Color SEAGREEN = new Color(0.18039216f, 0.54509807f, 0.34117648f);
	public static final Color SEASHELL = new Color(1.0f, 0.9607843f, 0.93333334f);
	public static final Color SIENNA = new Color(0.627451f, 0.32156864f, 0.1764706f);
	public static final Color SILVER = new Color(0.7529412f, 0.7529412f, 0.7529412f);
	public static final Color SKYBLUE = new Color(0.5294118f, 0.80784315f, 0.92156863f);
	public static final Color SLATEBLUE = new Color(0.41568628f, 0.3529412f, 0.8039216f);
	public static final Color SLATEGRAY = new Color(0.4392157f, 0.5019608f, 0.5647059f);
	public static final Color SLATEGREY = SLATEGRAY;
	public static final Color SNOW = new Color(1.0f, 0.98039216f, 0.98039216f);
	public static final Color SPRINGGREEN = new Color(0.0f, 1.0f, 0.49803922f);
	public static final Color STEELBLUE = new Color(0.27450982f, 0.50980395f, 0.7058824f);
	public static final Color TAN = new Color(0.8235294f, 0.7058824f, 0.54901963f);
	public static final Color TEAL = new Color(0.0f, 0.5019608f, 0.5019608f);
	public static final Color THISTLE = new Color(0.84705883f, 0.7490196f, 0.84705883f);
	public static final Color TOMATO = new Color(1.0f, 0.3882353f, 0.2784314f);
	public static final Color TURQUOISE = new Color(0.2509804f, 0.8784314f, 0.8156863f);
	public static final Color VIOLET = new Color(0.93333334f, 0.50980395f, 0.93333334f);
	public static final Color WHEAT = new Color(0.9607843f, 0.87058824f, 0.7019608f);
	public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f);
	public static final Color WHITESMOKE = new Color(0.9607843f, 0.9607843f, 0.9607843f);
	public static final Color YELLOW = new Color(1.0f, 1.0f, 0.0f);
	public static final Color YELLOWGREEN = new Color(0.6039216f, 0.8039216f, 0.19607843f);
}
