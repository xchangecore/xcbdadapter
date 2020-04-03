package com.spotonresponse.adapter.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import org.springframework.web.multipart.MultipartFile;

public class CSVToJSON {

    public static List<Map<String, Object>> parse(MultipartFile file) {

        try {
            CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
            CsvMapper csvMapper = new CsvMapper();

            // Read data from CSV file
            ObjectMapper oMapper = new ObjectMapper();
            List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
            List<Object> lines = csvMapper.readerFor(Map.class).with(csvSchema).readValues(file.getInputStream())
                    .readAll();
            for (Object line : lines) {
                rows.add(oMapper.convertValue(line, Map.class));
            }
            return rows;
        } catch (Exception e) {
            // TODO some error handler
            return null;
        }
    }
}