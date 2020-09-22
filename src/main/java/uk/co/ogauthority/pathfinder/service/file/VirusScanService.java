package uk.co.ogauthority.pathfinder.service.file;

import org.springframework.web.multipart.MultipartFile;

public interface VirusScanService {

  boolean hasVirus(MultipartFile file);

}