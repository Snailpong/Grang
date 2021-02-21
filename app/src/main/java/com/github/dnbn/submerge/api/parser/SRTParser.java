package com.github.dnbn.submerge.api.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.dnbn.submerge.api.parser.exception.InvalidSRTSubException;
import com.github.dnbn.submerge.api.parser.exception.InvalidSubException;
import com.github.dnbn.submerge.api.subtitle.srt.SRTLine;
import com.github.dnbn.submerge.api.subtitle.srt.SRTSub;
import com.github.dnbn.submerge.api.subtitle.srt.SRTTime;

/**
 * Parse SRT subtitles
 */
public final class SRTParser extends BaseParser<SRTSub> {

	@Override
	protected void parse(BufferedReader br, SRTSub sub) throws IOException, InvalidSubException {

		boolean found = true;
		int idx = 0;
		while (found) {
			SRTLine line = firstIn(idx, br);
			if (found = (line != null)) {
				sub.add(line);
				idx++;
			}
		}
	}

	/**
	 * Extract the firt SRTLine found in a buffered reader. <br/>
	 * 
	 * Example of SRT line:
	 * 
	 * <pre>
	 * 1
	 * 00:02:46,813 --> 00:02:50,063
	 * A text line
	 * </pre>
	 * 
	 * @param br
	 * @return SRTLine the line extracted, null if no SRTLine found
	 * @throws IOException
	 * @throws InvalidSRTSubException
	 */
	private static SRTLine firstIn(int idx, BufferedReader br) throws IOException, InvalidSRTSubException {

		String idLine = readFirstTextLine(br);
		String timeLine = br.readLine();

		if (idLine == null || timeLine == null) {
			return null;
		}

		int id = parseId(idLine);
		SRTTime time = parseTime(timeLine);

		List<String> textLines = new ArrayList<>();
		String testLine;
		while ((testLine = br.readLine()) != null) {
			if (StringUtils.isEmpty(testLine.trim())) {
				break;
			}
			textLines.add(testLine);
		}

		return new SRTLine(idx, time, textLines);
	}

	/**
	 * Extract a subtitle id from string
	 * 
	 * @param textLine ex 1
	 * @return the id extracted
	 * @throws InvalidSRTSubException
	 */
	private static int parseId(String textLine) throws InvalidSRTSubException {

		int idSRTLine;
		try {
			idSRTLine = Integer.parseInt(textLine.trim()) - 1;
		} catch (NumberFormatException e) {
			throw new InvalidSRTSubException("Expected id not found -> " + textLine);
		}

		return idSRTLine;
	}

	/**
	 * Extract a subtitle time from string
	 * 
	 * @param timeLine: ex 00:02:08,822 --> 00:02:11,574
	 * @return the SRTTime object
	 * @throws InvalidSRTSubException
	 */
	private static SRTTime parseTime(String timeLine) throws InvalidSRTSubException {

		SRTTime time = null;
		String times[] = timeLine.split(SRTTime.DELIMITER.trim());

		if (times.length != 2) {
			throw new InvalidSRTSubException("Subtitle " + timeLine + " - invalid times : " + timeLine);
		}

		try {
			long start = SRTTime.fromString(times[0]);
			long end = SRTTime.fromString(times[1]);
			time = new SRTTime(start, end);
		} catch (Throwable e) {
			throw new InvalidSRTSubException("Invalid time string : " + timeLine, e);
		}

		return time;
	}

}
